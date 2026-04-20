from huggingface_hub import hf_hub_download
import pandas as pd
import os
# Latest release of fishbase is available from Hugging Face
# https://huggingface.co/

REPO_ID = 'cboettig/fishbase'
FILENAME = 'data/fb/v25.04/parquet/species.parquet'
FILENAME2 = 'data/fb/v25.04/parquet/estimate.parquet'


BASEDIR = '/home/bpasquer/Documents/NRMN_RLS_ATRC/DAY2/FishBase/rfishbase_Update/'
FISHBASE_DATA_DIR = 'fb_parquet_2025-07'
NRMN_OBS_ITEM_REF = 'nrmn_prod_nrmn_observable_item_ref-Jan2026.csv'
NRMN_LW_REF = 'nrmn_prod_nrmn_lengthweight_ref-Jan2026.csv'  # not all obsitem have LW coeffs

SUPERSEDING = 'nrmn_prod_species_with_superseding-Jan2026.csv'
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
    df = pd.read_csv(os.path.join(BASEDIR, NRMN_LW_REF))
    return df

def get_superseding():
    df = pd.read_csv(os.path.join(BASEDIR, SUPERSEDING))
    return df

if __name__ == "__main__":

    fishbase_df = get_species_info()

    nrmn_df = get_nrmn_observable_item_ids()
    lw_id_df = get_obsitem_id_with_lw_attributes()
    superseding = get_superseding()

    # consolidate LW with nrmn_df
    nrmn_tempo = pd.merge(nrmn_df, lw_id_df, how='left', left_on='observable_item_id', right_on='observable_item_id')

    # add superseding info into nrmn_df
    nrmn_info = pd.merge(nrmn_tempo, superseding, how='left', left_on='observable_item_id',right_on='observable_item_id')

    ## combined FB-NRMN
    df1 = pd.merge(fishbase_df, nrmn_info, how='left', left_on='species_name', right_on='observable_item_name')


    # dataframe for new coefficents(obs item with NO biomass coeff in DB)
    # because of the outer join,  df2 also contain obs item that are not in fishbase(superseded)

    ##### INSERT #########
    with open('LengthWeight_insert-Jan2026.sql', 'a') as f:
        f.write('BEGIN;\n')
        for row in df1.iterrows():
            if (str(row[1]['observable_item_name']) != 'nan') and \
                    pd.isna(row[1]['a_y']) and pd.isna(row[1]['b_y']) and \
                   not pd.isna(row[1]['a_x']) and not pd.isna(row[1]['b_x']):
                f.write('INSERT INTO nrmn.lengthweight_ref (observable_item_id, a, b, cf) VALUES(' +
                         str(int(row[1]['observable_item_id'])) + ',' +
                         str(round(row[1]['a_x'], 5)) + ',' + str(round(row[1]['b_x'], 2)) + ',1);\n')
        f.write('END;')
    f.close()
##### UPDATE #########
    with open('LengthWeight_update-Jan2026.sql', 'a') as f1:
        f1.write('BEGIN;\n')
        for row in df1.iterrows():
            if (str(row[1]['observable_item_name']) != 'nan') and \
                    not pd.isna(row[1]['a_y']) and \
                    not pd.isna(row[1]['b_y']):
                f1.write('UPDATE nrmn.lengthweight_ref SET a=' + str(round(row[1]['a_x'],5)) + ',b=' + str(
                    round(row[1]['b_x'],2)) + ',cf=1 where observable_item_id=' + str(int(row[1]['observable_item_id'])) + ';\n')
        f1.write('END;')
    f1.close()
##### SUPERSEDING #########
    with open('LengthWeight_superseded-Jan2026-insert.sql', 'a') as f2:
        f2.write('BEGIN;\n')
        for row in df1.iterrows():
            if (str(row[1]['superseded_ids']) != 'nan') and \
                    not pd.isna(row[1]['a_x'] ) and \
                    not pd.isna(row[1]['b_x']):
                #Query will be insert if no existing value in LW dataframe, otherwise update:
                superseded = row[1]['superseded_ids']
                for id in superseded.split(','):  #case where multiple superseded_ids
                    if not lw_id_df.loc[lw_id_df['observable_item_id'] == int(id)].empty:  #update
                        f2.write('UPDATE nrmn.lengthweight_ref SET a=' + str(round(row[1]['a_x'], 5)) + ',b=' + str(
                            round(row[1]['b_x'], 2)) + ',cf=1 where observable_item_id=' + str(id).strip() + ';\n')

                    else:  #insert
                        pass
                        #f2.write('INSERT INTO nrmn.lengthweight_ref (observable_item_id, a, b, cf) VALUES(' +
                        #             str(id).strip() + ',' +
                        #             str(round(row[1]['a_x'], 5)) + ',' + str(round(row[1]['b_x'], 2)) + ',1);\n')
        f2.write('END;')
    f2.close()


# generate comparison file
#     with open('LengthWeight_comparison.txt', 'a') as f4:
#         f4.write('observable_item_id, observable_item_name, nrmn_a, FB_a, nrmn_a,  nrmn_b, FB_b, nrmn_cf, FB_cf ;\n')
#         for row in df1.iterrows():
#             if (str(row[1]['a']) != 'nan' and str(row[1]['observable_item_id']) != 'nan'):
#                 f4.write(str(
#                     int(row[1]['observable_item_id'])) + ',' + str(row[1]['observable_item_name']) + ',' +
#                          str(round(row[1]['a_nrmn'], 5)) + ',' +
#                         str(round(row[1]['a'], 5)) + ',' + str(
#                     round(row[1]['b_nrmn'], 2)) + ',' + str(
#                     round(row[1]['b'], 2)) + ',' + str(
#                     round(row[1]['cf_nrmn'], 2)) + ', 1;\n')
#
#     f4.close()