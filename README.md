# TrailCompanion App - Android App For Cycling Computer (IoT Project)
## Overview

**TrailCompanion** is an app designed to track your bike rides. It connects to a device that measures various parameters, such as braking intensity, acceleration, top speed, distance, average speed, and current location. Based on the data received from the device, the app maps the route following the user's location and colors it according to braking intensity. Users can save their rides and view them later in the "**Trails**" menu, where they can check the total distance, top speed, and average speed.

# How Does It Work?

To use the app, the user must ensure that the device is powered on, the app is installed, and Bluetooth is enabled.
1. In the **main menu**, press the "**GO**" button.
2. The **Connection Manager** screen will appear. Tap "**Connect Device**" to start searching for available devices.
3. Once the device is found, it connects via Bluetooth, and a "**Start**" button appears at the bottom of the screen.
4. Pressing "**Start**" begins data transmission from the device and the route is mapped in real time.
5. At the bottom of the screen, a "**Stop**" button is available. Pressing it stops data collection and prompts the user to **save** the trail.

## Where Are The Rides Saved?

Currently, all ride data is stored locally on the device. In a future update, the app will support cloud storage, allowing users to save their rides in a database. If there is no internet connection, the data will be stored internally and synced once a connection is available.
