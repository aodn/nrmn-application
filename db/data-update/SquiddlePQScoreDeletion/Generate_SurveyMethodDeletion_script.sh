# to work the csv has to be delimited by ';' and the text unquoted
while read -r line; 
   	do
       surveymethodid=`echo $line | cut -d';' -f2`;
       echo "DELETE FROM nrmn.survey_method where survey_method_id="$surveymethodid=";" >>SurveyMethodDeletion.sql 
    done < SurveyMethodToDelete_formatted.csv 
