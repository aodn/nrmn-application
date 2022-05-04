the task consisted in identifying all pqscores scored in the squiddle and ausralian corla species resolution and deleted them from the database. 
The deletion also involve removing survey_method_id with no more associated data
this folder contains 4 types of files:
-SQL scripts to extract the data that needs to be deleted: SquiddleScoreInNRMNQuery.sql,FindSurveyMethodToDelete.sql
-the result of the query in csv, and a second version reformatted:PQScoreToDeleteFromNRMN.csv+_formatted , SurveyMethodToDelete.csv+_formatted
-the shell script to generate the final sql query to be run againt the databases:Generate_PQscoreDeletion_script.sh,Generate_SurveyMethodDeletion_script.sh
- the final queries:PQscoreDeletion.sql,SurveyMethodDeletion.sql

 
