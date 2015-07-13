#! /bin/bash

# Start the CliSeAu with ICAP server
java -jar SafeShopping.jar &

# Start CliSeAu instance only
java -jar SafeShopping.jar 7 &
java -jar SafeShopping.jar 8 &
java -jar SafeShopping.jar 29 &
java -jar SafeShopping.jar 33 &
java -jar SafeShopping.jar 37 &
java -jar SafeShopping.jar 48 &
java -jar SafeShopping.jar 51 &
java -jar SafeShopping.jar 60 &
java -jar SafeShopping.jar 63 &

echo 'System has been successfully initialized'
