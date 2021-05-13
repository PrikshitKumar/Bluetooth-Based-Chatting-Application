# Bluetooth-Based-Chatting-Android-Application
As we can chat with anyone on Whatsapp using the Internet. Same way, we can chat using this Application as well. But in it, instead of the Internet, Bluetooth is used. And, the range of Bluetooth is approximately 10 Meters. So, we can connect with any device using this app that is within the range of 10 Meters.

https://user-images.githubusercontent.com/64913497/116663738-27646e00-a9b5-11eb-99ce-f35331489098.mp4

This video contains the working of my android project.
This video helps you to understand the functionality of this application.

Basically, in First Activity, it is mandatory to fill both the fields **"Name"** as well as **"Profession"**. Otherwise, it forces you to fill both of these fields.

After that, in the second Activity, Bluetooth ON/OFF, Paired Devices, Available Devices, and some buttons are there. When we click on the "**Reload"** button, it searches again for the Available and Paired Devices. And, when we click on the **"Make Device Discoverable"**, this enables the visibility of your device for 10 seconds. And last, when you click on the **"Listener"** button, then the Bluetooth Socket should be ready to accept the Connections. Then, you have to click on the **device name** (Name of the device in which you clicked "Listener"), from the Second Device or Client Device. Then, the control is going to the new activity "Activity 3".

In activity 3, both the devices can communicate with each other. They can send the messages and receive the messages as well. And, all the Connection states like Connect the device or not, the connection is failed or success, etc. are shown to you by toast, or a text view, that is attached below the **"Name"** Field in Activity 3. All the states are mentioned there.

After pressing the back button, or close the Application, all the connections should be terminated.


This is how this application **"BlueChat"** Works.
