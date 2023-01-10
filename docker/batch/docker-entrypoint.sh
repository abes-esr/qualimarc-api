#!/bin/bash

# Paramètres par défaut du conteneur
export QUALIMARC_BATCH_CRON=${QUALIMARC_BATCH_CRON:='0 50 6 1 * *'}
export QUALIMARC_BATCH_CRON_FLUSH=${QUALIMARC_BATCH_CRON_FLUSH:='0 0 0 1 1 *'}
export QUALIMARC_BATCH_AT_STARTUP=${QUALIMARC_BATCH_AT_STARTUP:='1'}

# Réglage de /etc/environment pour que les crontab s'exécutent avec les bonnes variables d'env
echo "$(env)
LANG=en_US.UTF-8" > /etc/environment

# Charge la crontab depuis le template
envsubst < /etc/cron.d/tasks.tmpl > /etc/cron.d/tasks
echo "-> Installation des crontab :"
cat /etc/cron.d/tasks
crontab /etc/cron.d/tasks

# Force le démarrage du batch au démarrage du conteneur
if [ "$QUALIMARC_BATCH_AT_STARTUP" = "1" ]; then
  echo "-> Lancement de qualimarc-batch.sh au démarrage du conteneur"
  /scripts/qualimarc-batch.sh
fi

# execute CMD (crond)
exec "$@"