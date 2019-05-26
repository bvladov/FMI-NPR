#NPR Project
Client-server application. The client sends a log file to the server with this structure:
Time;Event context;Component;Event name;Description;Origin;IP address
The server processes the file using RP-Growth itemset mining algorithm and send back the results.

RP-Growth implementation by:
http://www.philippe-fournier-viger.com/spmf/FPGrowth.php
