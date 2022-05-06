# to work the csv has to be delimited by ';' and the text unquoted
# note : une premiere version de l'update a ete foyrnie, puis un revised version, qui est la version finale pour laquelle le fichier untitled.ods a ete utilisr pour merger les donner
while read -r line; 
   	do
   	   #echo "LINE",$line
       scoreid=`echo $line | cut -d';' -f1`;
       echo "DELETE FROM nrmn.pq_score where score_id="$scoreid";" >>PQscoreDeletion_Additional.sql 
    done < PQScoreToDelete-Additional_Formatted.csv