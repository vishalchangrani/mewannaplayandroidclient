#!/bin/bash
clear
echo -e "Starting user creation...\n"
echo "Creating $1 users with username prefix as $2"
i=0;

while [ $i -lt $1 ]; do
	
	echo "Posting message for: $2_$i"
	rm -rf newcookies.txt;
	

	CMD="`curl -s -i --cookie-jar newcookies.txt  -X POST -d '{\"user\":{\"username\":\"'$2_$i'\",\"password\": \"fbd6f1ccYW1pdGdhbmRoaQ==\"}}' --location http://api.mewannaplay.com/v1/users/login`";

	if [ $? -ne 0 ]; then
		echo "Non zero exit status";
		echo $CMD;
		exit 1;
	fi

	echo $CMD;
	RESULT="`echo "$CMD" | grep -c -i  '"error":"Yes"'`";
	if [ $RESULT -ne 0 ]; then
		echo "Error in response ";		
		exit 1;
	fi


	CMD="`curl -v --cookie newcookies.txt --cookie-jar newcookies.txt -X GET --location http://api.mewannaplay.com/v1/markoccupied/id/108`";
	
	if [ $? -ne 0 ]; then
		echo "Non zero exit status";
		echo $CMD;
		exit 1;
	fi

	echo $CMD;
	RESULT="`echo "$CMD" | grep -c -i  '"error":"Yes"'`";
	if [ $RESULT -ne 0 ]; then
		echo "Error in response ";		
		exit 1;
	fi	

	CMD="`curl -s -i --cookie newcookies.txt --cookie-jar newcookies.txt --location http://api.mewannaplay.com/v1/users/logout`";

	if [ $? -ne 0 ]; then
		echo "Non zero exit status";
		echo $CMD;
		exit 1;
	fi

	echo $CMD;
	RESULT="`echo "$CMD" | grep -c -i  '"error":"Yes"'`";
	if [ $RESULT -ne 0 ]; then
		echo "Error in response ";		
		exit 1;
	fi


	let i=i+1;
done

exit 0
