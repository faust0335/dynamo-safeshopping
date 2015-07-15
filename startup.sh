#! /bin/bash

# Start the CliSeAu with ICAP server
java -jar SafeShopping.jar &

# Start CliSeAu instance only
java -jar SafeShopping.jar localhost 7 &
java -jar SafeShopping.jar localhost 8 &
java -jar SafeShopping.jar localhost 29 &
java -jar SafeShopping.jar localhost 33 &
java -jar SafeShopping.jar localhost 37 &
java -jar SafeShopping.jar localhost 48 &
java -jar SafeShopping.jar localhost 51 &
java -jar SafeShopping.jar localhost 60 &
java -jar SafeShopping.jar localhost 63 &

echo 'System has been successfully initialized'
