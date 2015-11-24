Problem Statement :
 You are helping a restaurant owner who is really excited about social media by creating a REST service for him that advertises on Twitter.

•      The restaurant owner wants to tweet daily specials from their restaurant’s Twitter account
•      Your customers should be able to order by tweeting at your restaurant with a specific hashtag
•      The service should read the order.
•      Respond to the customer with an ETA and a thank you.
•      The orders should be persisted.
•      The owner should be able to delete any orders that have been saved.
•      The owner should be able to get pending orders.

Assumptions :
1. Client has authorization to access the service. In otherway, only authorized clients can access the REST APIs
2. Each menu item is hashtag.Hence order is set of hashtags 
	Sample tweet order : todays special #chickenburger #cheeseburger #cheesepizza #chickenpizza #tandooripizza #fishtacos #chickentacos #veggieburget #veggiepizza
3. Assuming customer orders only one valid item of each type.
4. ETA is kept as 10 min for any order
5. Only single item is allowed to delete at a time
6. Restaurant owner is going invoke REST call whenever there is update to the menu
7. Menu will be updated every day at some time. Until then previous day menu is consider as valid or until when owner is going to invoke updatemenu end point
8. Owner can delete both pending and completed orders
9. When customer want to order, he/she needs to include #takemyorder any where in the order. otherwise order wont be consider
10. Current timezone is considered as PST
11. All Order/Pending orders does not include deleted orders



Pending :
1.Customer order needs to be validated against the menu
	Idea : we can make order invalid and update the status/internal structure accordingly. 


REST API :
To get all the orders : http://localhost:4080/1.1/restaurant/orders?ispending=false
To get pending orders : http://localhost:4080/1.1/restaurant/orders?ispending=true
To get deleted orders : http://localhost:4080/1.1/restaurant/orders?isdeleted=true
	Note : when ispending=true then priority is given to return pending order which are not deleted
To fetch/update daily menu items from twitter : http://localhost:4080/1.1/restaurant/updatemenu
To delete order : curl -X delete -d 663742054904664000 http://localhost:4080/1.1/restaurant/orders


