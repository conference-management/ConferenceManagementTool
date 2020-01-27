## Building the Project

### Building the Backend
Building the project requires gradle. For this please refer to: [https://gradle.org/install/](https://gradle.org/install/)
Inside the 'Backend' folder type in the command `gradle :fatJar`
This will produce a jar under `./build/libs/fullCMS-1.0.jar

### Building the Frontend
The frontend can be found in the 'frontend' folder.  The 'config/config.js' file must be edited so that it contains the correct domain. This is the domain at which the ***jar*** is hosted, which may or may not be the same as the domain which hosts the frontend itself.
Changing the port is only necessary if one changed it inside the backend.
The property `useSSL` must be set to true (`false` is only permitted for debugging purposes)

One can customize the frontend if they wish to do so. For example it is possible to chage the images inside the  'img' folder as long as the new images have the same name. Please note that the images might get scaled or cropped in the process of rendering the page. As such it is recomended to provide images that have the same dimiension as the original images.

### For developers

Note that it is recomended to install the standard node modules for testing purposes (https://nodejs.org/api/modules.html)

## Deploying the Project

Delpoy the frontend like any other webpage.

It is recomended but not necesarry to have the root pointing to 'frontend/index.html'. 
 
Copy the jar created by the `gradle :fatJar` command in an ***empty*** directory.
Add a configuration file to the directory. The file provided in `examples/config.txt` provides a well documented description on what config files should look like. 
The connection must be encrypted using SSL. The SSL certificate must be placed inside the `pem` directory and have the names: `cert.pem` and ``privkey.pem``

Start the backend using the command `java -jar fullCMS-1.0.jar start ./config.txt`. If everything was set up properly the application should print the data and the passwords of all admins in a format similar to:
```
Name : Test
Email : test@test.com
Residence : test
Group : test
Function : test
Username : Test
password : test
```

Additionally this information and login QR codes are also stored inside the "tmp/conference/qrs" directory. The login codes can be scanned using any QR code scanner.
`Note:` Admins aren't permitted to interfere with other admins, meaning that the config file is the only method to edit an admin. Also, admins can login multiple times using the same password while attendee passwords get invalidated after being used once.

### Additional File Support

The application supports two types of files that are meant to ease conference management:

 - Agenda files: agenda files end with ".txt" and contain the TOPs of the conference. Each line represents a individual TOP and must be prefixed with its order. The example provided looks as follows:

```
1 The first TOP
1.1 The first sub-TOP
2 Second Order
2.1 An other subtopic
2.2 An an other
2.2.1 The last

```

- Attendee files: attendee files end with ".csv" and contain attendee data. They can be used to add large number of attendees. The format is the same as the admin format in the config files. The example provided looks as follows:

```
name:email@mail.com:group:residence:function
test:test@test.com:test:test:test
```


## Usage

Note that in the following usage specifications of the application, the term "attendees" includes admins by default, which means admins are attendees that are granted additional rights.
Every time only attendees without administrative rights are considered, the description will specify them as such.

**Important Note for Admins:** Keep in mind that for most of the requests sent to the server, IDs are being used that can change if anything of the same type gets deleted,
e.g. deleteing an agenda topic will result in the topic IDs changing. Because of that, keeping an old version of the page and trying to edit/delete something can result in editing/deleteing something you did not want to, since IDs were changing in between.
Thus, try to keep a recent version of the page if you wish to do something while multiple admins are working on the page simultaneously.

### Agenda

For ordinary **attendees** without administrative power, the agenda can be accessed, but not edited or interacted with in any way.

While the agenda is empty, **admins** can create its first topic using the "Add topic" button on the bottom left.

Admins can furthermore expand the agenda, using the plus icon to **add a topic** on the same level, while the down arrow can be used to **add a subtopic** to that respective topic. The new topic will always get the next ID after the ID of the clicked topic on the level it is appended to. The topics with an equal or higher ID will just move one spot further accordingly.

The pencil/trash icons **edit**/**delete** their respective topic. Note that deleting a topic will also delete its subtopics automatically.

Also, the file icon in the header can be clicked to upload an agenda file as specified in the "Additional File Support" section. Clicking the delete icon in the header will result in deleting the whole agenda.


### Documents

This page displays a list of documents uploaded to the server. All attendees are allowed to **download** them by either clicking on the name of the document or on the download icon next to it.
Furthermore, a **revision number** labeled with "version:" next to each document will tell the attendees how often the respective document has been edited. Documents will be sorted by upload time and uploading new documents to the server will result in them being the last item on the document list.

Admins will be granted the right to **upload** documents to the server. To do that, they can click on the "File select" button on the bottom left, which will open the system's file browser and let them choose the file they wish to upload.
To confirm the selection and thus upload the file, the "Submit" button right next to the file selection can be clicked.

Furthermore, admins can **edit** documents by clicking the pencil icon next to the document they wish to edit. Again, this option will open the system's file browser and let them select a file that shall replace the old file.
Note that only files with the same file extension will be accepted, e.g. editing a ".pdf" document will only allow admins to replace it with another ".pdf" document. Editing a document will increment its revision number by 1.

Admins can also **delete** documents by clicking the trash icon next to the document they wish to delete. This document will then be removed from the server for all attendees.


### Voting

This page is divided into three sections: "Active Voting", "Previous Votings" and "Edit/Create Votings".

In the "Active Voting" section, attendees can submit their vote to the **active voting** by selecting one of the radio buttons on the left and clicking the "Submit Vote" button afterwards. If there is no active voting or if the active voting message expired, an according message will show instead of the voting here.
When there is a new active voting, all attendees will get redirected to this page once and a **timer** will appear representing the remaining time to vote. This timer will show up in the header as well and clicking it will redirect attendees to this page.
Once the timer has run out, the voting counts as expired and no more votes can be submitted for it.

In the "Previous Votings" section, the results of old votings will display. The clickable **show details** text next to the voting questions will make that section expand and display the amount of votes that has been submitted for each option. For named votings, the name of every person that has picked that option will display as well.

In the "Edit/Create Votings" section which is only accessible for admins, new votings can be created, edited, deleted or started. Clicking on an existing voting will make that section expand and show the voting options as well as buttons to interact with that voting.

When **creating** a voting by clicking the "New Voting" button, the user can decide whether it shall be a named vote, what the voting question should be and how long the voting should take by filing in the respective fields in the popup dialog.

Existing votings can be **started** by clicking the "Start" button. There can only be one active voting at the same time and starting a voting will make it the active one, removing it from this section and displaying it in the first section, handling it as described above.

Clicking the "Add" button will **add new voting options** to that voting, making a text field appear to input the name of the voting option. This input field can always be modified and clicking the "Save Changes" button will save these modifications.
Starting a voting will save all changes automatically, as well. Clicking the **Remove** button next to an option will remove that option from the voting. Note that a voting can't be started unless it has two or more options.
Furthermore, if abstentions should be displayed in the results of the "Previous Votings" section, a voting option for that has to be added manually.

**Important:** Note that only finished votings will be stored into the database persistently. If the server backend gets shut down in between, all created votings without final results will be lost.
Furthermore, always keep in mind that **only** attendees marked as **present** will be granted the right to vote.


### Requests

When hovering over the "Requests" tab in the header, attendees can choose two different options: "Request of Change" or "Request of Speech". Admins can also choose the option "Manage Requests".
Note that the **topic** of a request can not only be a topic of the current agenda, but also a document that has been uploaded.

At the **"Request of Change"** page, attendees can select the topic they want to request a change for by selecting it in the dropdown menu at the top.
Additionally, a request text can be written in the input field beneath before clicking the "Request" button at the bottom to submit the request.

The **"Request of Speech"** page works completely analogous to the "Request of Change" page apart from the missing input field for a request text. Here, attendees can request to talk about a certain request topic.

All requests done by attendees will get displayed in the **"Manage Requests"** page that only admins have access to. The table will display the request topic in the "Requests" column, the exact time when it was submitted in the "Time Stamp" column, the request type (request of change or of speech) in the "Request Type" column and the current status ("open", "closed", "approved", "disapproved") in the "Status" column. Requests will be considered "open" by default.
Clicking on a request will cause its section to expand and tell the admin who sent the request and, in case it is a request of change, display its request text.

In case of a request of change, when the section of a request is expanded, a tick icon and an "x" icon can be found to the right. Clicking the tick icon will mark the request as **approved**, clicking the "x" icon will mark it as **disapproved**.
In both cases, the request will count as **closed**, but the "Status" section will give further information about its approval status.

In case of a request of speech, there is just a tick icon to the right giving the ability to mark them as **closed**.
Requests of speech cannot be given any approval or disapproval, which means their "Status" will only be considered "closed" without further information.

Note that open requests will always be displayed at the top and closed requests at the bottom. After that, requests get sorted by time stamp, showing the earliest requests first.

Also, the two dropdown menus at the top of this page can be used to only display requests of a certain type (requests of speech or of change) or only display requests to a certain request topic, which is called "Request Target" here.



### Profile

The "Profile" page displays the data of the attendee which is currently logged in. This data includes the **name**, **username**, **email**, **group**, **residence** and **function** of that attendee.
That data cannot be interacted with and (in case it is necessary) has to be edited by admins in the "User Management" page.


### User Management

The "User Management" page is only accessible by admins.

A list of attendees will display in a table and clicking on any attendee will make their section expand, showing the remaining data that's not displayed in the table itself yet as well as icons that can be clicked to interact with that attendee. The data displayed here is consistent with the data being shown in the "Profile" section of the respective attendees, giving additional information whether that attendee is currently **present** at the conference or not.

Changing the sorting relation in the dropdown menu at the top will cause the page to refresh and **sort** the attendee list below by that selected category. Sorting by **group** will sort the attendees by their group, in case they have the same group by function and after that by name.
Sorting by **function** will sort by function first, after that by group and after that by name. Sorting by **name** doesn't provide any secondary sorting relation.

When clicking the "Add Attenddee" button on the bottom left to **create a new attendee**, a popup window will show in which almost all data for the new attendee can be given using text input fields.
The only exceptions are the username, which will be generated automatically using the name of the Attendee, and the present status as all attendees will be given the present value "false" by default.
Passwords also will have to be generated seperately after the creation of the attendee. Clicking the "Confirm" button will try to create an attendee with the given data in case the data sticks to the requested format
(format violations will display at the top of the popup window). Clicking the "X" at the top or the "Cancel" button will close the popup window.

Clicking the pencil icon in the section of an attendee will also open a popup window and grant the possibility to **edit** the respective attendee. All data of the attendee can be changed again,
giving admins the additional possibility to change the present status of the attendee. Setting "Yes" means setting the present status to "true" while setting "No" means setting it to false. Again, the
"Confirm" button will save the changes after applying the same format control that is applied when adding new attendees. 
Note that admins always count as present as soon as they logged in for the first time.

Clicking the lock icon will generate a **new password** for the attendee, invalidating their old one. The new password will be shown to the admin in a popup window.

Clicking the logout icon will **log the attendee out** and leave them unable to do anything until logging in again, but their current password will stay safe (in case it didn't get destroyed by them logging in using that password).

The trash icon of an attendee can be clicked to **delete** them. Note that deleteing an attendee does also mean removing all their requests, but their votes will be kept.

Clicking the QR code icon will also generate a **new password** for the attendee, but provide it using a QR code that will download automatically.
Scanning this QR code will redirect the attendee to the hosted URL and log them in at once.

Clicking the file icon in the header of the User Management section will open the admin's file browser and give them the possibility to upload a CSV attendee file as specified in the "Additional File Support" section.
Attendees being added using a CSV file will be scanned for the same format as if they would have been added using the "Add Attendee" button each.

Clicking the download icon in the header of the User Management section will generate new passwords for all attendees and store their data into a ".zip" file including their passwords stored as QR codes.



## Contributers:
(1) Muhammad Kamran - Frontend Layout + Frontend Voting Functionality

(2) Jessica Werner - Database + Testing + Backend + Minor Frontend Functionality Implementation

(3) Stephan Ariesanu - Backend + Testing + Minor Frontend Functionality Implementation

(4) Giuliano Rasper - Communication

(5) Alexander Dincher - Database + Backend

(6) Simon Fedick - Backend + Frontend User Management Functionality
