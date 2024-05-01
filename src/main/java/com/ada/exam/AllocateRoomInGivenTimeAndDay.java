package com.ada.exam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AllocateRoomInGivenTimeAndDay {
    // This function returns list of rooms that can be allocated
    public static List<Integer> findClosestSum(int[] roomSizes, int totalStudents, double percent) {
        List<Integer> result = new ArrayList<>();
        List<Integer> size_of_rooms = new ArrayList<>();
        for(int i=0; i<roomSizes.length; i++) {
            size_of_rooms.add(roomSizes[i]);
        }
        int sum = 0;
        Collections.sort(size_of_rooms, Collections.reverseOrder());
        int percentage = totalStudents;
        while(sum < percentage && !size_of_rooms.isEmpty()) {
            Collections.sort(size_of_rooms, Collections.reverseOrder());
            int room_size = size_of_rooms.get(0);
            for(int j=0; j<size_of_rooms.size()-1; j++) {
                int room_size1 = (int) Math.ceil(size_of_rooms.get(j) * percent);
                int room_size2 = (int) Math.ceil(size_of_rooms.get(j+1) * percent);
                if(room_size1 >= (percentage-sum) && (percentage-sum) > room_size2) {
                    room_size = size_of_rooms.get(j);
                    size_of_rooms.remove(Integer.valueOf(size_of_rooms.get(j)));
                    break;
                }
            }
            sum += (int) Math.ceil(room_size * percent);
            result.add(room_size);
            size_of_rooms.remove(Integer.valueOf(room_size));
        }
        return result;
    }
    // Compare two CRN
    public static boolean CompareTwoCRN(int crn1, int crn2, MultipartFile filename) {
        List<Integer> listOfStudentsCRN1 = getListOfStudentId(crn1, filename);
        List<Integer> listOfStudentsCRN2 = getListOfStudentId(crn2, filename);
        for (int i=0; i<listOfStudentsCRN1.size(); i++) {
            for (int j=0; j<listOfStudentsCRN2.size(); j++) {
                if(listOfStudentsCRN1.get(i) == listOfStudentsCRN2.get(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    // TODO: student.txt
    // it will give you list of students' ID for given CRN
    public static List<Integer> getListOfStudentId(int crn, MultipartFile file) {
        List<Integer> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(",");
                int crn1 = Integer.valueOf(array[0].trim());
                if (crn1 == crn) {
                    for (int i = 1; i < array.length; i++) {
                        int student_id = Integer.valueOf(array[i].trim());
                        list.add(student_id);
                    }
                }
            }
        } catch (IOException ioException) {
            System.out.println("problem with getListOfStudentId");
        }
        return list;
    }

    public static List<Integer> giveListOfRoomSizes(MultipartFile filename) {
        List<Integer> listOfRooms = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filename.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(",");
                int room_capacity = Integer.parseInt(array[3].trim());
                listOfRooms.add(room_capacity);
            }
        } catch (IOException ioException) {
            System.out.println("Problem with giveListOfAvailableRoomsGivenTime");
        }
        return listOfRooms;
    }

    public static List<String> listOfAllRooms(MultipartFile filename) {
        List<String> rooms = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filename.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(",");
                rooms.add(array[1].trim());
            }
        } catch (IOException exception) {
            System.out.println("Problem with listOfAllRooms");
        }
        return rooms;
    }


    // it returns number of sits for all rooms
    public static HashMap<String, Integer> mappingRoomAndCapacity(MultipartFile filename) {
        HashMap<String, Integer> rooms = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filename.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(",");
                String room = array[1].trim();
                int room_capacity = Integer.parseInt(array[3].trim());
                rooms.put(room, room_capacity);
            }
        } catch (IOException e) {
            System.out.println("Problem with Room!");
        }
        return rooms;
    }


    public static List<String> result(MultipartFile roomFile, MultipartFile studentFile, double percent) {
        GraphColoring graphColoring = new GraphColoring();
        List<String> resultList = new ArrayList<>();
        HashMap<Integer, List<Student>> csvFile = graphColoring.readingFromFile(studentFile);

        // All colors.
        HashMap<Integer, List<Integer>> colors = GraphColoring.coloring(csvFile, studentFile);
        System.out.println(colors);
        int day = 1; //...
        // time max can be 5;
        int time = 1;
        for(Integer color: colors.keySet()) {
            List<Integer> crn = colors.get(color);

            // List of all room's name...
            List<String> room_names = listOfAllRooms(roomFile);
            // List of all room's sizes...
            List<Integer> room_sizes = giveListOfRoomSizes(roomFile);
            // Map of room names and room size.
            HashMap<String, Integer> name_size = mappingRoomAndCapacity(roomFile);



            for(int i=0; i<crn.size(); i++) {
                int number_of_students = getListOfStudentId(crn.get(i), studentFile).size();

                int array[] = new int[room_sizes.size()];
                for (int j = 0; j < room_sizes.size(); j++) {
                    array[j] = room_sizes.get(j);
                }
                // This part will give us rooms' sizes for given number.
                List<Integer> rooms = findClosestSum(array, number_of_students, percent);

                List<String> room = roomNames(name_size, rooms);
                room_sizes = roomSizesToBeDeleted(rooms, room_sizes);
                room_names = roomNamesToBeDeleted(room, room_names);
                name_size = mapToBeDeleted(name_size, room);

                // CRN, hour, day, rooms(List)
                String line = crn.get(i) + ", ";
                String t = timeConverter(time);
                String d = dayConverter(day);
                line += t + ", " + d + ", ";
                for (int x = 0; x < room.size()-1; x++) {
                    line += room.get(x) + ",";
                }
                line += room.get(room.size() - 1);
                resultList.add(line);
            }
            time += 1;

            if (time == 6) {
                time = 1;
                day += 1;
            }
            if (day == 7) {
                break;
            }
        }
        return resultList;
    }

    public static List<String> roomNames(HashMap<String, Integer> name_size, List<Integer> roomSizes) {
        List<String> rooms = new ArrayList<>();

        for(int i=0; i<roomSizes.size(); i++) {
            for(String room: name_size.keySet()) {
                int size = name_size.get(room);

                if(size == roomSizes.get(i)) {
                    rooms.add(room);
                    name_size.remove(room);
                    break;
                }
            }
        }

        return rooms;
    }

    public static List<String> roomNamesToBeDeleted(List<String> deletedRooms, List<String> rooms) {
        for(int i=0; i<deletedRooms.size(); i++) {
            rooms.remove(deletedRooms.get(i));
        }
        return rooms;
    }
    public static List<Integer> roomSizesToBeDeleted(List<Integer> deletedRooms, List<Integer> rooms) {
        for(int i=0; i<deletedRooms.size(); i++) {
            rooms.remove(deletedRooms.get(i));
        }
        return rooms;
    }
    public static HashMap<String, Integer> mapToBeDeleted(HashMap<String, Integer> name_size, List<String> rooms) {
        for(int i=0; i<rooms.size(); i++) {
            name_size.remove(rooms.get(i));
        }
        return name_size;
    }

    public static String timeConverter(int time) {
        if(time == 1) return "08:30 - 10:30";
        if(time == 2) return "10:45 - 12:45";
        if(time == 3) return "13:00 - 15:00";
        if(time == 4) return "15:15 - 17:15";
        if(time == 5) return "17:30 - 19:30";
        return "";
    }

    public static String dayConverter(int day) {
        if(day == 1) return "Day I";
        if(day == 2) return "Day II";
        if(day == 3) return "Day III";
        if(day == 4) return "Day IV";
        if(day == 5) return "Day V";
        if(day == 6) return "Day VI";
        return "";
    }

    public static void finalResult(List<String> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.csv"))) {
            for (String line : list) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("File write successful!");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
