from huggingface_hub import hf_hub_download
import pandas as pd
import os
# Latest release of fishbase is available from Hugging Face
# https://huggingface.co/

######  Code  for Third FishBase update - uses Fishbase 2024 version #####################
###### note the source is different from first 2 updates ###############

REPO_ID = 'cboettig/fishbase'
FILENAME = 'data/fb/v24.07/parquet/species.parquet'
FILENAME2 = 'data/fb/v24.07/parquet/estimate.parquet'


BASEDIR = '/home/bpasquer/Documents/NRMN_RLS_ATRC/DAY2/FishBase/rfishbase_Update/'
FISHBASE_DATA_DIR = 'fb_parquet_2024-07'
NRMN_OBS_ITEM_REF = 'nrmn_prod_nrmn_observable_item_ref.csv'
NRMN_LW_REF = 'Obs_Item_withLW.csv'  # not all obsitem have LW coeffs
NRMN_LW_REF_VAL = 'Obs_Item_withLW_values2.csv' # same as above with values (for comparison)
#species = pd.read_parquet(hf_hub_download(repo_id=REPO_ID, filename=FILENAME, repo_type="dataset"))
#estimate = pd.read_parquet(hf_hub_download(repo_id=REPO_ID, filename=FILENAME2, repo_type="dataset"))

def get_species_info():
    # parse data from fish base extract to dataframe
    # return dataframe with species name, species fishbase code and coeffs
    df1 = pd.read_parquet(hf_hub_download(repo_id=REPO_ID, filename=FILENAME, repo_type="dataset"))
    species_info = df1[['SpecCode', 'Genus', 'Species']]

    df2 = pd.read_parquet(hf_hub_download(repo_id=REPO_ID, filename=FILENAME2, repo_type="dataset"))
    estimate = df2[['SpecCode', 'a', 'b']]

    res = pd.merge(left=species_info, right=estimate, how='inner', left_on='SpecCode', right_on='SpecCode')
    res['species_name'] = res['Genus'] + ' ' + res['Species']

    return res

def get_nrmn_observable_item_ids():
    # parse nrmn obs item info from file extract into dataframe
    df = pd.read_csv(os.path.join(BASEDIR, NRMN_OBS_ITEM_REF))
    return df

def get_obsitem_id_with_lw_attributes():
    df = pd.read_csv(os.path.join(BASEDIR, NRMN_LW_REF_VAL))
    return df

if __name__ == "__main__":

    fishbase_df = get_species_info()

    nrmn_df = get_nrmn_observable_item_ids()
    lw_id_df = get_obsitem_id_with_lw_attributes()

    merged_df = pd.merge(nrmn_df, fishbase_df, how='inner', left_on='observable_item_name', right_on='species_name')

    # dataframe for updated coefficents(obs item with biomass coeff in DB)
    df1 = pd.merge(fishbase_df, lw_id_df, how='left', left_on='species_name', right_on='observable_item_name')

    with open('LengthWeight_update.txt', 'a') as f:
        f.write('BEGIN;\n')
        for row in df1.iterrows():
            if str(row[1]['observable_item_name']) != 'nan':
                f.write('UPDATE nrmn.lengthweight_ref SET a=' + str(round(row[1]['a'],5)) + ',b=' + str(
                    round(row[1]['b'],2)) + ',cf=1 where observable_item_id=' + str(int(row[1]['observable_item_id'])) + ';\n')
        f.write('END;')

    f.close()
    # dataframe for new coefficents(obs item with NO biomass coeff in DB)
    # because of the outer join,  df2 also contain obs item that are not in fishbase(superseded)
    df2 = pd.merge(merged_df, lw_id_df, how='outer', left_on='species_name', right_on='observable_item_name')
    with open('LengthWeight_insert.txt', 'a') as f2:
        f2.write('BEGIN;\n')
        for row in df2.iterrows():
            if str(row[1]['observable_item_name_y']) == 'nan':
                f2.write('INSERT INTO nrmn.lengthweight_ref (observable_item_id, a, b, cf) VALUES(' +
                         str(int(row[1]['observable_item_id_x'])) + ',' +
                         str(round(row[1]['a'],5)) + ',' + str(round(row[1]['b'],2))+',1);\n')
        f2.write('END;')

    f2.close()

# Warning script  is not meant to be used in Prod. It is only necessary because we have applied the insert in the
# testing envioronment and connot re-insert values.

    with open('LengthWeight_inserted_value_update.txt', 'a') as f3:
        f3.write('BEGIN;\n')
        for row in df2.iterrows():
            if str(row[1]['observable_item_name_y']) == 'nan':
                f3.write('UPDATE nrmn.lengthweight_ref SET a=' + str(round(row[1]['a'], 5)) + ',b=' + str(
                    round(row[1]['b'], 2)) + ',cf=1 where observable_item_id=' + str(
                    int(row[1]['observable_item_id_x'])) + ';\n')
        f3.write('END;')

    f3.close()


# generate comparison file
    with open('LengthWeight_comparison.txt', 'a') as f4:
        f4.write('observable_item_id, observable_item_name, nrmn_a, FB_a, nrmn_a,  nrmn_b, FB_b, nrmn_cf, FB_cf ;\n')
        for row in df1.iterrows():
            if (str(row[1]['a']) != 'nan' and str(row[1]['observable_item_id']) != 'nan'):
                f4.write(str(
                    int(row[1]['observable_item_id'])) + ',' + str(row[1]['observable_item_name']) + ',' +
                         str(round(row[1]['a_nrmn'], 5)) + ',' +
                        str(round(row[1]['a'], 5)) + ',' + str(
                    round(row[1]['b_nrmn'], 2)) + ',' + str(
                    round(row[1]['b'], 2)) + ',' + str(
                    round(row[1]['cf_nrmn'], 2)) + ', 1;\n')

    f4.close()