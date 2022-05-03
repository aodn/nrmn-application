# to work the csv has to be delimited by ';' and the text unquoted
while read -r line; 
   	do
   	   #echo "LINE",$line
       scoreid=`echo $line | cut -d';' -f1`;
       echo "DELETE FROM nrmn.pq_score where score_id="$scoreid";" >>PQscoreDeletion.sql 
    done < PQScoreToDeleteFromNRMN_formatted.csv
