java -jar build/libs/meep-service-1.0.jar


curl -H "Content-Type: application/json" -X POST -d '{"sender":"smarti", "facebookId": "13212314", "picture":"http://www.fpic.com/123123.jpeg", "receipts":"[{"id":"asdasd"}, {"id":"123dsa"}]"}' http://127.0.0.1:4567/meeps
