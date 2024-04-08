package src.backend.lodging;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Booking {
    private DateRange dateRange;
    private String userName;
    private Lodging lodgeName;
    private static List<Booking> bookings = new ArrayList<>();

    public Booking(){}

    public Booking(DateRange dateRange, String userName, Lodging lodgeName) {
        this.dateRange = dateRange;
        this.userName = userName;
        this.lodgeName = lodgeName;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Lodging> getBookings(String manager)
    {
        List<Lodging> managerBookings = bookings.stream()
                .filter(booking -> booking.getLodgeName().getManager().equals(manager))
                .map(Booking::getLodgeName)
                .collect(Collectors.toList());

        return (ArrayList<Lodging>) managerBookings;
    }

    public Lodging getLodgeName() {
        return lodgeName;
    }

    public void setLodgeName(Lodging lodgeName) {
        this.lodgeName = lodgeName;
    }

    public static boolean addBooking(DateRange dateRange, String userName, Lodging lodgeName) {
        for (Booking booking : bookings) {
            if (booking.getLodgeName().equals(lodgeName) && booking.getDateRange().isWithinRange(dateRange.getFrom(), dateRange.getTo())) {
                System.out.println("Booking conflict! The lodge is already booked for the specified date range.");
                return false;
            }
        }

        Booking newBooking = new Booking(dateRange, userName, lodgeName);
        bookings.add(newBooking);
        System.out.println("Booking successful!");
        return true;
    }
}
