import java.util.ArrayList;
class Main {
    public static void main(String[] args) {
        User host = new User("Jose Doe", "jose@gmail.com", "123 Sesame St");

        Event event = new EventBuilder()
                .setVenue("My House")
                .setDate("This Saturday")
                .setTitle("Birthday Party")
                .setLink("www.facebook.com/event/23729")
                .setHost(host)
                .setTheme(EventType.BIRTHDAY)
                .build();


        User ayisha = new User("Ayisha Smith", "ayisha@gmail.com", "124 Sesame St");
        event.inviteGuest(ayisha);
        event.guestAttending(ayisha);

        User martha = new User("Martha Stewart", "marta@gmailcom", "102 Main St");
        event.inviteGuest(martha);
        event.guestDeclined(martha);

        event.getResponseDetails();
    }
}

class User {
    private String name;
    private String email;
    private String address;
    private ArrayList<Event> invitedEvents = new ArrayList<Event>();
    private ArrayList<Event> acceptedEvents = new ArrayList<Event>();
    private ArrayList<Event> declinedEvents = new ArrayList<Event>();
    private ArrayList<Event> eventsHosting = new ArrayList<Event>();

    public User(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;

    }
   public void invitedEvent(Event event) {
       this.invitedEvents.add(event);
   }

    public void acceptedEvent(Event event) {
        this.acceptedEvents.add(event);
    }

    public void declinedEvent(Event event) {
        this.declinedEvents.add(event);
    }

    public void hostingEvent(Event event) {
        this.eventsHosting.add(event);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) { 
            return true; 
        } 
        if (!(o instanceof User)) { 
            return false; 
        }  
        User user = (User) o;
        return this.name == user.name && this.email == user.email && this.address == user.address;
    }

    public String getName() {
        return this.name;
    }

    // will be used when notifying guests of invitations and hosts of rsvps
    public void sendMessage(String message) {
        System.out.print(message);
    }
}

enum EventType {
    BIRTHDAY, WEDDING, GRADUATION, GET_TOGETHER
}

class EventBuilder {
    private String title;
    private String venue;
    private String date;
    private String link;
    private User host;
    private EventType eventType;

    public EventBuilder setHost(User host) {
        this.host = host;
        return this;
    }

    public EventBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public EventBuilder setDate(String date) {
        this.date = date;
        return this;
    }

    public EventBuilder setVenue(String venue) {
        this.venue = venue;
        return this;
    }

    public EventBuilder setLink(String link) {
        this.link = link;
        return this;
    }

    public EventBuilder setTheme(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public Event build() {
        return new Event(this.title, this.venue, this.date, this.link, this.host, this.eventType);
    }
}

class Event {
    private String title;
    private String venue;
    private String date;
    private String link;
    private User host;
    private EventType eventType;
    private Notification invitation;
    private ArrayList<User> invitedGuests = new ArrayList<User>();
    private ArrayList<User> acceptedGuests = new ArrayList<User>();
    private ArrayList<User> declinedGuests = new ArrayList<User>();

    public Event(String title, String venue, String date, String link, User host, EventType eventType) {
        this.title = title;
        this.venue = venue;
        this.date = date;
        this.link = link;
        this.host = host;
        this.eventType = eventType;
        this.invitation = createInvitation(eventType);
        this.host.hostingEvent(this);
    }

    private Notification createInvitation(EventType eventType) {
        switch (this.eventType) {
            case BIRTHDAY:
                return new BirthdayInvitation(new Invitation());
            case GRADUATION:
                return new GraduationInvitation(new Invitation());
            case WEDDING:
                return new WeddingInvitation(new Invitation());
            case GET_TOGETHER:
            default:
                return new GetTogetherInvitation(new Invitation());
        }
    }

    public void inviteGuest(User user) {
        if (!this.invitedGuests.contains(user)) {
            this.invitedGuests.add(user);
            user.invitedEvent(this);
            this.sendInviteToGuest(user);
        } else {
            throw new Error("Guest has already been invited");
        }
    }

    private void sendInviteToGuest(User user) {
        String eventDescription = this.eventToString();
        String invitationMessage = this.invitation.getMessage(user, eventDescription);
        user.sendMessage(invitationMessage);
    }

    public void guestAttending(User user) {
        for (int i = 0; i < this.invitedGuests.size(); i++) {
            User currentUser = this.invitedGuests.get(i);
            if (currentUser.equals(user)) {
                this.invitedGuests.remove(i);
                this.acceptedGuests.add(user);
                this.notifyHostOfRsvp(user, true);
            }
        }
    }
    public void guestDeclined(User user) {
        for (int i = 0; i < this.invitedGuests.size(); i++) {
            User currentUser = this.invitedGuests.get(i);
            if (currentUser.equals(user)) {
                this.invitedGuests.remove(i);
                this.declinedGuests.add(user);
                this.notifyHostOfRsvp(user, false);
            }
        }
    }

    private void notifyHostOfRsvp(User attendingUser, boolean isAttending) {
        String attendingUserName = attendingUser.getName();
        String extraString = isAttending ? "" : " not";
        String message = "The following guest: " + attendingUserName + ", is" + extraString + " going to your event!";
        String hostNotification = new HostNotification().getMessage(this.host, message);
        this.host.sendMessage(hostNotification);
    }
    
    private String eventToString() {
        return "\nTitle: " + this.title + "\nDate: " + this.date + "\nHost: "+ this.host.getName() + "\nVenue: " + this.venue + "\nLink: " + this.link + "\nGuests Attending: " + this.acceptedGuests.size();
    }

    public void getResponseDetails() {
        int totalInvitesSent = this.acceptedGuests.size() + this.declinedGuests.size() + this.invitedGuests.size();
        int guestsAttending = this.acceptedGuests.size();
        int guestsRejected = this.declinedGuests.size();
        System.out.print("Total Invitations Sent: " + totalInvitesSent + ". Total Guests Attending: " + guestsAttending + ". Total Guests Rejected: " + guestsRejected);
    }

}

class HostNotification implements Notification {
    @Override
    public String getMessage(User receipient, String message) {
        return "\nDear " + receipient.getName() + ",\n" + message;
    }
}

interface Notification {
    public String getMessage(User receipient, String message);
}

class Invitation implements Notification {

    @Override
    public String getMessage(User receipient, String message) {
        return "Dear " + receipient.getName() + ",\n You're invited to:" + message;
    }
}

abstract class InvitationDecorator implements Notification {
    protected Notification invitationToBeDecorated;
    public InvitationDecorator(Notification invitationToBeDecorated){
        this.invitationToBeDecorated = invitationToBeDecorated;
    }

    public String getMessage(User receipient, String message) {
        return invitationToBeDecorated.getMessage(receipient, message);
    }
}

class BirthdayInvitation extends InvitationDecorator {

    private Notification inviteToBeDecorated;

    public BirthdayInvitation(Notification inviteToBeDecorated) {
        super(inviteToBeDecorated);
        this.inviteToBeDecorated = inviteToBeDecorated;
    }

    @Override
    public String getMessage(User receipient, String message){
        String header = "\nBIRTHDAY PARTY \n";
        return header + this.inviteToBeDecorated.getMessage(receipient, message);
    }
}


class GraduationInvitation extends InvitationDecorator {
    private Notification inviteToBeDecorated;
    public GraduationInvitation(Notification inviteToBeDecorated) {
        super(inviteToBeDecorated);
        this.inviteToBeDecorated = inviteToBeDecorated;
    }

    @Override
    public String getMessage(User receipient, String message){
        String header = "\nGRADUATION \n";
        return header + this.inviteToBeDecorated.getMessage(receipient, message);
    }
}


class WeddingInvitation extends InvitationDecorator {
    private Notification inviteToBeDecorated;
    public WeddingInvitation(Notification inviteToBeDecorated) {
        super(inviteToBeDecorated);
        this.inviteToBeDecorated = inviteToBeDecorated;
    }

    @Override
    public String getMessage(User receipient, String message){
        String header = "\nWEDDING \n";
        return header + this.inviteToBeDecorated.getMessage(receipient, message);
    }
}

class GetTogetherInvitation extends InvitationDecorator {
    private Notification inviteToBeDecorated;
    public GetTogetherInvitation(Notification inviteToBeDecorated) {
        super(inviteToBeDecorated);
        this.inviteToBeDecorated = inviteToBeDecorated;
    }

    @Override
    public String getMessage(User receipient, String message){
        String header = "\nGET TOGETHER \n";
        return header + this.inviteToBeDecorated.getMessage(receipient, message);
    }
}
