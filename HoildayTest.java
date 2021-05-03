import java.util.*;

public class HoildayTest {
    static Map<String, Hoilday> hoildays = new HashMap<>();
    static int[] months = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static void main(String[] args) {
        // Use the Scanner class
        Scanner sc = new Scanner(System.in);
        boolean wrongInput = false;

        while (!wrongInput) {
            try {
                System.out.println("Please enter the Start Date as three Integers in the following order: year, month, day without leading zeros");
                int startYear = sc.nextInt();
                int startMonth = sc.nextInt();
                int startDay = sc.nextInt();

                System.out.println("Please enter the End Date as three Integers in the following order: year, month, day without leading zeros");

                int endYear = sc.nextInt();
                int endMonth = sc.nextInt();
                int endDay = sc.nextInt();

                var startDate = Calendar.getInstance();
                startDate.set(startYear, startMonth - 1, startDay);

                var endDate = Calendar.getInstance();
                endDate.set(endYear, endMonth - 1, endDay);

                if (startDate.after(endDate)) {
                    System.out.println("Start Date cannot be later than the End Date.");
                    wrongInput = true;
                } else {
                    startDate.add(Calendar.DATE, 1);
                    endDate.add(Calendar.DATE, -1);

                    startYear = startDate.get(Calendar.YEAR);
                    startMonth = startDate.get(Calendar.MONTH) + 1;
                    startDay = startDate.get(Calendar.DATE);
                    endYear = endDate.get(Calendar.YEAR);
                    endMonth = endDate.get(Calendar.MONTH) + 1;
                    endDay = endDate.get(Calendar.DATE);

                    int totalWeekdays = 0;

                    if (startYear == endYear) {
                        totalWeekdays += getWeekdaysWithHoildays(startYear, startMonth, startDay, endMonth, endDay);
                    } else {
                        // calculate weekdays for the first partial year
                        totalWeekdays += getWeekdaysWithHoildays(startYear, startMonth, startDay, 12, 31);

                        // calculate whole years in between
                        for (int currentYear = startYear + 1; currentYear < endYear; ++currentYear) {
                            totalWeekdays += getWeekdaysWithHoildays(currentYear, 1, 1, 12, 31);
                        }

                        // calculate weekdays for the last partial year
                        totalWeekdays += getWeekdaysWithHoildays(endYear, 1, 1, endMonth, endDay);
                    }

                    System.out.println("Total weekdays: " + totalWeekdays);
                }

            } catch (Exception e) {
                System.out.println("Input format is wrong, process stopped.");
                wrongInput = true;
            }
        }
        sc.close();
    }

    // get weekdays between two dates for the current year, start and end dates are inclusive
    private static int getWeekdaysWithHoildays(int year, int startMonth, int startDay, int endMonth, int endDay) {
        System.out.println("Start year: " + year);
        // reset hoildays to initial state
        resetHoildays();

        // get weekdays without considering hoildays
        int weekdays = getWeekdaysIfNoHoildays(year, startMonth, startDay, endMonth, endDay);

        // set the exact dates for all hoildays given the current year value
        setExactDateForAllHoildays(year);
        for (String key : hoildays.keySet()) {
            var hoilday = hoildays.get(key);
            // check if hoilday is in range
            if (hoildayInRange(hoilday, startMonth, startDay, endMonth, endDay)) {
                if (!hoilday.willSubstitute && !hoilday.willAlwaysBeWeekday) {
                    // Anzac day, substract weekdays by 1 if it is not on weekends
                    var calendar = Calendar.getInstance();
                    calendar.set(year, hoilday.month - 1, hoilday.day);
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                        weekdays--;
                        System.out.println("Excluding: " + hoilday.name + " " + hoilday.month + " " + hoilday.day);
                    }
                } else {
                    // substract weekdays by 1 for each hoilday in range
                    weekdays--;
                    System.out.println("Excluding: " + hoilday.name + " " + hoilday.month + " " + hoilday.day);
                }
            }
        }
        System.out.println("Weekdays at year " + year + ": " + weekdays);
        System.out.println("End year: " + year);
        return weekdays;
    }

    // calculate weekdays without considering hoildays
    private static int getWeekdaysIfNoHoildays(int year, int startMonth, int startDay, int endMonth, int endDay) {
        int totalDays = 0;
        for (int i = startMonth + 1; i < endMonth; ++i) {
            totalDays += months[i];
            if (i == 2 && isLeapYear(year)) {
                totalDays++;
            }
        }

        if (startMonth == endMonth) {
            totalDays += endDay - startDay + 1;
        } else {
            // add days from the start month
            totalDays += months[startMonth] - (startDay - 1);

            // add days from the end month
            totalDays += endDay;
        }

        int totalWeekdays = totalDays / 7 * 5;
        var calendar = Calendar.getInstance();
        calendar.set(year, startMonth - 1, startDay);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int remainingDays = totalDays % 7;
        for (int i = 0; i < remainingDays; ++i) {
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                totalWeekdays++;
            }
            dayOfWeek++;
            if (dayOfWeek > 7) dayOfWeek %= 7;
        }
        System.out.println("Weekdays without Hoildays at " + year + ": " + totalWeekdays);
        return totalWeekdays;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private static void setExactDateForAllHoildays(int year) {
        Set<Integer> alreadySubstituteDays = new HashSet<>();
        for (String key : hoildays.keySet()) {
            var hoilday = hoildays.get(key);
            if (hoilday.willSubstitute) {
                setSubstituteHoildays(hoilday, year, alreadySubstituteDays);
            } else if (hoilday.willAlwaysBeWeekday) {
                if (hoilday.name.equals("Good Friday") || hoilday.name.equals("Easter Monday")) {
                    setEasterHoildays(year);
                } else {
                    setLabourDay(hoildays.get("Labour Day"), year);
                    setQueensBirthday(hoildays.get("Queen's Birthday"), year);
                }
            }
        }
    }

    // for hoildays on weekends, find next available weekday and assign the new Date to Hoilday object
    // Some weekdays may be already taken by other hoildays, so we need to keep track of all weekdays that are taken.
    // Keep looking for next available weekday until find.
    // Save (month * 100 + date) to a Set, so we only need an Integer Set
    private static void setSubstituteHoildays(Hoilday hoilday, int year, Set<Integer> alreadySubstituteDays) {
        var cal = Calendar.getInstance();
        cal.set(year, hoilday.month - 1, hoilday.day);
        int currentHoildayDate = hoilday.day;
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            currentHoildayDate += 2;
        } else if (dayOfWeek == Calendar.SUNDAY) {
            currentHoildayDate += 1;
        }
        while (alreadySubstituteDays.contains(hoilday.month * 100 + currentHoildayDate)) {
            currentHoildayDate++;
        }
        hoilday.day = currentHoildayDate;
        alreadySubstituteDays.add(hoilday.month * 100 + currentHoildayDate);
    }

    // Formula to calculate Easter Sunday
    // Original Link is: https://stackoverflow.com/questions/26022233/calculate-the-date-of-easter-sunday
    private static void setEasterHoildays(int year) {
        int a = year % 19,
            b = year / 100,
            c = year % 100,
            d = b / 4,
            e = b % 4,
            g = (8 * b + 13) / 25,
            h = (19 * a + b - d - g + 15) % 30,
            j = c / 4,
            k = c % 4,
            m = (a + 11 * h) / 319,
            r = (2 * e + 2 * j - k - h + m + 32) % 7,
            n = (h - m + r + 90) / 25,
            p = (h - m + r + n + 19) % 32;

        var easterMonday = hoildays.get("Easter Monday");
        easterMonday.month = n;
        easterMonday.day = p + 1;
        
        // Easter Sunday can only be at April or May, if date is less than 2
        // Good Friday will be at previous month, which must be April
        var goodFriday = hoildays.get("Good Friday");
        if (p <= 2) {
            goodFriday.month = n - 1;
            goodFriday.day = p - 2 + 30;
        } else {
            goodFriday.month = n;
            goodFriday.day = p - 2;
        }
    }

    private static void setQueensBirthday(Hoilday hoilday, int year) {
        int day = calculateSpecialHoildayDate(year, Calendar.JUNE, 2, Calendar.MONDAY);

        hoilday.day = day;
        hoilday.month = 6;
    }

    private static void setLabourDay(Hoilday hoilday, int year) {
        int day = calculateSpecialHoildayDate(year, Calendar.OCTOBER, 1, Calendar.MONDAY);

        hoilday.day = day;
        hoilday.month = 10;
    }

    // Find Date for special hoildays
    // e.g. Queen's Birthday is second Monday in June
    // so numberOfOccurance is 2 (second), dayOfWeek is 1 (Monday)
    private static int calculateSpecialHoildayDate(int year, int startMonth, int numberOfOccurance, int dayOfWeek) {
        var calendar = Calendar.getInstance();
        int currentDay = 1;
        calendar.set(year, startMonth, currentDay);
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentOccurance = 0;
        while (currentOccurance < numberOfOccurance) {
            if (currentDayOfWeek == dayOfWeek) {
                currentOccurance++;
                if (currentOccurance == numberOfOccurance) {
                    break;
                }
            }
            currentDayOfWeek++;
            if (currentDayOfWeek > 7) currentDayOfWeek %= 7;
            currentDay++;
        }

        return currentDay;
    }

    private static boolean hoildayInRange(Hoilday hoilday, int startMonth, int startDay, int endMonth, int endDay) {
        return (hoilday.month > startMonth && hoilday.month < endMonth) 
                || (hoilday.month == startMonth && hoilday.day >= startDay)
                || (hoilday.month == endMonth && hoilday.day <= endDay);
    }

    private static void resetHoildays() {
        hoildays = new HashMap<>();
        hoildays.put("New Year's Day", new Hoilday("New Year's Day", true, false, 1, 1));
        hoildays.put("Australia Day", new Hoilday("Australia Day", true, false, 1, 26));
        hoildays.put("Good Friday", new Hoilday("Good Friday", false, true));
        hoildays.put("Easter Monday", new Hoilday("Easter Monday", false, true));
        hoildays.put("Anzac Day", new Hoilday("Anzac Day", false, false, 4, 25));
        hoildays.put("Queen's Birthday", new Hoilday("Queen's Birthday", false, true));
        hoildays.put("Labour Day", new Hoilday("Labour Day", false, true));
        hoildays.put("Christmas Day", new Hoilday("Christmas Day", true, false, 12, 25));
        hoildays.put("Boxing Day", new Hoilday("Boxing Day", true, false, 12, 26));
    }

    public static class Hoilday {
        private String name;
        private boolean willSubstitute;
        private boolean willAlwaysBeWeekday;
        private int month = 0;
        private int day = 0;

        public Hoilday(String name, boolean willSubstitute, boolean willAlwaysBeWeekday, int month, int day) {
            this.name = name;
            this.willSubstitute = willSubstitute;
            this.willAlwaysBeWeekday = willAlwaysBeWeekday;
            this.month = month;
            this.day = day;
        }

        public Hoilday(String name, boolean willSubstitute, boolean willAlwaysBeWeekday) {
            this.name = name;
            this.willSubstitute = willSubstitute;
            this.willAlwaysBeWeekday = willAlwaysBeWeekday;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getMonth() {
            return this.month;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getDay() {
            return this.day;
        }
    }
}