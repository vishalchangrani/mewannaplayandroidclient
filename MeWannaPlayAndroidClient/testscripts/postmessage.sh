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


	CMD="`curl -s -i --cookie newcookies.txt --cookie-jar newcookies.txt  -d '{\"data\":{\"Message\":{\"contact_type_id\":1,\"schedule_time\":\"2012-09-02 03:30:00\",\"player_type\":\"Beginner\",\"message_text\":\"Message from '$2_$i'\",\"player_needed\":\"1\",\"message_id\":0,\"contact_info\":\"7133928501\",\"tennis_court_id\":108}}}' --location http://api.mewannaplay.com/v1/tenniscourtmessages/add`";
	
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
