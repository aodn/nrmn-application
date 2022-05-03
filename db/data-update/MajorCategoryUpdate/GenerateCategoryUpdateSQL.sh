# to work the csv has to be delimited by ';' and the text unquoted
# note: the script uses the revised version(thefinal version) of the categoryname change provided on the 29/04/2022

while read -r line; 
   	do
   	   #echo "LINE",$line
       categoryid=`echo $line | cut -d';' -f1`;
       categoryname=`echo $line | cut -d';' -f4`;
       echo "UPDATE nrmn.pq_category_ref SET major_category_name='"$categoryname"' where category_id="$categoryid";" >>CategoryUpdate_revised.sql 
    done < MajorCategoryName_revisedupdate.csv
