import numpy as np
import os
import pandas as pd

## Script for the ad-hoc update of the NRMN DB to add taxonomic information of superseded species in the
## observable_item_ref table as it had be deleted in the source database
## This scripts extract the observable_item_id and data in the fileds phylum,class,order,family from the file
## superseded_species_missing_taxonomy.csv
## and output a sql file of the query to ingest the data in the table observable_item_ref

df = pd.read_csv(os.path.join(os.getcwd(), 'superseded_species_missing_taxonomy.csv'), header=1)

df = df.astype({'observable_item_id': np.int})
# sql query output file
filepath = os.path.join(os.getcwd(), 'superseded_species_missing_taxonomy.sql')
with open(filepath, 'w') as out:
    for index, row in df.iterrows():
        out.write(
            'UPDATE nrmn.observable_item_ref SET phylum =\'{phylum}\',"class" =\'{sp_class}\',"order"=\'{order}\',family ='
        '\'{family}\' WHERE observable_item_id = {obsid};\n'.
            format(phylum=row["phylum"], sp_class=row["class"], order=row["order"], family=row["family"],
                   obsid=row["observable_item_id"]))
