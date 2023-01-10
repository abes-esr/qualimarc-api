if [[ $(pgrep -cf "qualimarc-batch.jar --spring.batch.job.names=flushStatistiques") = 0 ]];
then
    java -jar qualimarc-batch.jar --spring.batch.job.names=flushStatistiques
fi