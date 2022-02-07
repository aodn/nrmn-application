import numpy as np
import os
import pandas as pd
## Script for the ad-hoc update of the NRMN DB to ingest a MappingID for all species in the observable_item_ref table
## This scripts extract the data from the column MAPPING_ID of the csv file IMAS-NRMN_SpeciesIDmapping.csv
## and output a sql file of the query to ingest the data in the table observable_item_ref

df =pd.read_csv(os.path.join(os.getcwd(),'IMAS-NRMN_SpeciesIDmapping.csv'), header=1)
# remove nan value for debris before conversion to integer
df = df.dropna(subset=['MAPPED ID'])

df = df.astype({'MAPPED ID': np.int})
# sql query output file
filepath = os.path.join(os.getcwd(),'SpeciesID_mapping.sql')
with open(filepath, 'w') as out:
    for index,row in df.iterrows():
        out.write('UPDATE nrmn.observable_item_ref SET mapped_id ={mappedid} WHERE observable_item_id= {obsid};\n'.
        format(mappedid=row["MAPPED ID"],obsid= row["OBSERVABLE ITEM ID"]))
