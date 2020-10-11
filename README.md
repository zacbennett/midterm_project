# COMPSCIX418.2 Midterms

## Classes:
 - User
    - Description: Houses all events the user is hosting and attening. Sends messages to the user (or in this case just prints them)
    - Operations: Storing the users events, sending the user messages
 - Event
    - Description: Houses the event information and all event logic (ex: when to send notifications)
    - Operations: handling when a user RSVPs (accepts or rejects), sending notifications to the user and the host
 - Event Builder
     - Description: Handles instantiating the Event object
     - Operations: Setting the different Event params, instantiating the Event object
 - HostNotification
     - Description: Handles creating the messages for the host
     - Operations: Creating the template for messages to the host
 - InvitationDecorator
    - Description: Allows us to decoreate the different invitations, allowing us to add additional features (or headers) to the different invites
    - Operations: Creating the template for messages to the guests
 - BirthdayInvitation
 - WeddingInvitation
 - GraduationInvitation
 - GetTogetherInvitation

## Design Patterns Used:
1. Decorator: Because we want the different invitations to have similar, yet slightly different functionality, I used a decorator. This allows each of the invitations to have a specific heading at the top of the invite, while still creating our standard invitation.
2. Builder: Because the Event class has a lot of parameters, I used a builder pattern to make instantiating the object easier.
3. Factory (for creating the different invites): Because we have different invitations, I decided to use a factory pattern in the createInvitation method. This allows us to pass in an enum and receive a Notification.
4. Subscriber/Observer: The user is observing changes in the event state. When the event state changes (when a user decides to RSVP), we send the host a notification. The user is also observing when they are invited to an event. When this happens, we send them an invitation. 
