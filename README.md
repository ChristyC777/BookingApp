# Booking Application #

## Manager Functions ##

1) Add accommodations.
2) Add available dates for accommodations.
3) View bookings for accommodations they own.
4) Management via console application.
5) Display total bookings by region for a specific time period.

## Tenant Functions ##

1) Filtering (location, date, number of people, price, stars).
2) Booking accommodations.
3) Rating (1–5 stars).
4) Actions via Android UI:
        - search(): Sends filters to the Master asynchronously and displays the results.
        - book(): Makes a booking for an accommodation from those returned by the search().


_**Note:** To handle the data volume, the system must be able to run in a distributed manner across multiple machines._

## Tools ##

-	**MapReduce Framework:** A programming model enabling the parallel processing of large datasets, using the Map and Reduce functions
    - **Map(key, value):**
      
    Input: File lines, etc., as (key, value).
 	
    Transforms (key, value) → (key2, value2).

    Executes in parallel on different nodes with different inputs.
 	
    Note: The degree of parallelism depends on the application and is defined by the user.

    
    - **Reduce(key2, value2):** Merges intermediate value2 elements associated with the same key2, producing the final results.
      
    For each key, it generates: key → list(values).
    Note: Reduce processes data only after all Map functions are complete.
    
      _**Σημείωση:** Η επεξεργασία της συνάρτησης reduce γίνεται αφού έχει τελειώσει η επεξεργασία όλων των map συναρτήσεων._

-	**JSON** Used for adding accommodations in the following format:

    {
        "roomName": "roomName",
        "noOfPersons": 5,
        "area": "Area1",
        "stars": 3,
        "noOfReviews": 15,
        "roomImage": "/usr/bin/images/roomName.png"
    }

-	**Workers:** Nodes.

-	**Hashing:** Uses the room name as input for hashing.

## Procedures ##

Initial Communication:

- The manager's console app communicates with the Master by sending all accommodation data.
- Upon receiving the data, the Master uses a hash function H(roomName) to determine the worker node where the accommodation will be stored:
        Example: nodeID = H(roomName) mod NumberOfNodes.
- The Master then sends the information to the selected worker node.

_**Note:** The worker stores the information in appropriate data structures in its memory. Storing on disk is NOT allowed, except optionally for the photos if desired._

Worker Functionality:

-> Stores the accommodation data in appropriate in-memory data structures.

_**Note:** Disk storage is not allowed, except optionally for storing photos._

Tenant Search:

-> When a tenant wants to book an accommodation, they select the appropriate filters via the app.
-> A request containing the filters is sent to the Master.
-> The Master uses a MapReduce process to return accommodations that meet the criteria.
-> The results, including details and photos, are displayed to the tenant, who can proceed with booking.

Booking:

-> When a tenant books an accommodation, a booking request is sent to the Master.
-> The Master updates the Worker managing the accommodation to reflect the booking.
    
_**Note:** Measures must be taken to prevent simultaneous bookings for the same accommodation on the same dates._

## Backend ##

**Master:**
- Written in Java and acts as a TCP server.
        Restrictions:
            1- No third-party libraries (default ServerSocket of Java or HTTP protocol with a ready server only).
            2- Must be multithreaded, serving multiple requests simultaneously, and communicate with Workers.

**Workers:**
- Written in Java and also multithreaded to handle multiple requests from the Master.
- Dynamically defined during the Master’s initialization (number of workers can be set via arguments or config file).
- Communicate with the Master via TCP sockets.
- Use in-memory data structures, and disk storage is not allowed.

**Synchronization:**
        Use mechanisms such as synchronized, wait, and notify.
        Restrictions: Do not use java.util.concurrent or any other ready-made tools.

## Frontend ##

-> Application – Master Communication: Via TCP sockets.
-> Uses Threads.


### Relationship Diagram

![Relationship Diagram](/images/scheme.png)

### Domain Model

![Domain Model](/images/class_diagram.png)
