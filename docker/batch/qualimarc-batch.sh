if [[ $(pgrep -cf "qualimarc-batch.jar --spring.batch.job.names=exportStatistiques") = 0 ]];
then
    java -jar qualimarc-batch.jar --spring.batch.job.names=exportStatistiques
fi