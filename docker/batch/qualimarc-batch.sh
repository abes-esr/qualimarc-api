if [[ $(pgrep -cf "qualimarc-batch.jar exportStatistiques") = 0 ]];
then
    java -jar /scripts/qualimarc-batch.jar exportStatistiques
fi