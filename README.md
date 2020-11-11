# A Scalable Key And Trust Management Solution For IOT Using SDN And Blockchain
## Initial Requirements
* ### Downloading Ubuntu ISO Image
First visit the official website of Ubuntu from your favorite web browser. Once the page loads, click on Download.

* ### Clone Contiki Folder from the GitHub Repository
Clone the Contiki Folder from the GitHub Repository into your Virtual Machine.

* ### Optional (Contiki VM Direct)
In order to directly download and install Contiki VM, please follow the steps as mentioned in this [link](https://techflow360.com/quickstart-your-iot-experiments-by-quickly-deploying-instant-contiki/).
We have also shared the image of our VM with several test simulations on the following link. The root password of VM is @Welcome1

[SDN-BC-IoT.ova](https://drive.google.com/u/0/uc?id=1zrv2SziiD7vcYZycJMnVFs7zASmYDFFz&amp;export=download)

* ### Clone SDN-WISE Folder from the GitHub Repository
Clone the Contiki Folder from the GitHub Repository into your Virtual Machine. In order to execute SDN-WISE successfully on your VM, please follow the steps on this [link](https://sdnwiselab.github.io/docs/guides/GetStarted.html)
Note: Don’t clone the SDN-WISE Repository from the above link. Just follow the SDN-WISE execution steps from the above URL as the repository of the above URL does not contain the updated code. Updated Code is present in the SDN-
WISE Folder of the project repository.

* ### Install Multichain on your VM
In order to install Multichain on your Virtual Machine, please follow the steps as mentioned in this [link](https://www.multichain.com/download-community/)

* ### Clone the MultiChain Web Demo Repository in your VM
In order to clone Multichain Web Demo Code into your Virtual Machine and to execute your first chain of Multichain. Please follow the steps as mentioned in this [link](https://github.com/MultiChain/multichain-web-demo)

## Steps to execute the code on your VM
First and foremost, it is recommended that you create 3 streams in your default
multichain. Each named “test1”, “test2” and “test3”. You can alternatively change the
function calls to write to a stream of your own choosing but these are the default names
we have used. The process to create a stream should be explained in the web demo
GitHub page linked above.

Next, we need to initiate the Multichain server/daemon. To do so, simply open up a
terminal and type in the command “multichaind sdn-bc-iot –daemon” without the
quotation marks.

1. The first word after multichaind is the name of the multichain so always make
sure it fits with whatever name you applied. In our case, it’s “sdn-bc-iot” so that’s
what we’ll use.

1. Next, open up the folder containing Contiki, and browse into “tools” then into
“cooja”. Open up your terminal in that location.

1. Type in “sudo ant run” as you can see in the below picture. The contiki folder
does not need to be in that same spot, you can keep it on another spot in your
storage drive. Root privileges can be avoided by not typing sudo but it is
recommended that you utilize it if possible.

1. Next, wait for the Cooja Simulator to boot up fully. Once it is open, open the “File”
tab and click on New Simulation.

1. Follow the steps ahead to start a new simulation. Alternately if you already have
simulations saved then simply follow Step 5(a) and skip steps 6,7 and 8
regarding setting up a simulation.
   1. If you are opening an existing simulation, then simply select “File” in the top
left, then “Open Simulation” then either pick one of the listed simulations or
open any that you have saved beforehand.

1. Open up the “Motes” tab, go to Add Motes, then Create a New Mote Type, then
select “Import Java mote”.

1. From there, find the SDN-wise folder containing your necessary Mote classes.
Open up the “Sink.class” file and import ONLY ONE Sink mote. Just use the
default setting when it asks for a TCP socket port.
**Sink.class Location**: /home/Contiki/tools/cooja/examples/sdn-wise_java/build/com/github/sdnwiselab/sdnwise/cooja/Sink.class
**Mote.class Location**: /home/Contiki/tools/cooja/examples/sdn-wise_java/build/com/github/sdnwiselab/sdnwise/cooja/Mote.class

1. Next, go back to the Import Java mote but this time, select “Mote.class”. Add as
many motes as you want.

1. Now that our simulation is set up, open up the “01-GetStarted” folder containing
the SDN repository and open the terminal in that location. Type in the following
command as mentioned below in the screenshot to start the execution of the
SDN-WISE Controller.
Note: If any changes made to the code file of SDN-WISE, you have to type “mvn
package” to compile the updated code of SDN-WISE.

1. If it worked correctly, you’ll see “SDN-Wise Controller running….” on the same
terminal.

1. Once this command is running, click “Start” on the paused Cooja simulator.
Note: If you attempt to Start the simulation before activating the SDN-Controller,
you will receive a “Connection refused” error on the log page and the simulation
will not work until you start an SDN Controller and reload the simulation.

1. To confirm that everything is working, you can check “localhost/?chain=default&amp;”
on web browser (Firefox in case of this VM) and go to “View Streams”.

1. From there you can open the streams “test1, test 2 and test3” in order to confirm
that whether the simulation has executed correctly or not.
   1. Check the timestamps on each data entry to confirm whether or not it was successful.
   1. “test1” contains a timestamp, a “Valid-To” date which is usually one month after the timestamp, the respective mote’s 512-bit public key and the ID of the mote in question.
   1. “test2” is almost the same, only difference is that it hosts the 2048-bit public key instead of the 512-bit.
   1. Finally “test3” contains the mote’s ID, the mote’s experience with a particular mote, a unique token, a signature which is the encrypted token hash encrypted using a 512-bit key and a timestamp.

1. In order to stop the Cooja Simulator and SDN-Controller at the end, they can beclosed by typing ctrl+C or by closing their window, the multichain should be stopped “responsibly” using the command “multichain-cli sdn-bc-iot stop”.
