whenever sqlerror exit failure

ALTER TABLE SBTRoutedMessage
   MODIFY PARTITION orderrelated
      ADD VALUES (11, 12);
