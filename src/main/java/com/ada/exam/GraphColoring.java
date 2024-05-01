package com.ada.exam;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphColoring {
    public static HashMap<Integer, List<Student>> readingFromFile(MultipartFile file) {
        HashMap<Integer, List<Student>> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                String[] integers = line.split(",");
                int key = Integer.parseInt(integers[0].trim());
                List<Student> value = new ArrayList<>();
                for (int i = 1; i < integers.length; i++) {
                    long number = Long.parseLong(integers[i].trim());
                    value.add(new Student(number));
                }
                map.put(key, value);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return map;
    }


    public static HashMap<Integer, List<Student>> readingFromCSV(String filename, List<Integer> subjects_crn) {
        String line = "";
        HashMap<Integer, List<Student>> map = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while((line = br.readLine()) != null) {
                String[] integers = line.split(",");
                int key = Integer.valueOf(integers[0].trim());
                // check if list contains the specific CRN then i will be added to map.
                if(subjects_crn.contains(key)) {
                    List<Student> value = new ArrayList<>();
                    for(int i=1; i<integers.length; i++) {
                        long number = Long.valueOf(integers[i].trim());
                        value.add(new Student(number));
                    }
                    map.put(key, value);
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return map;
    }

    public static HashMap<Integer, List<Integer>> coloring(HashMap<Integer, List<Student>> subjects, MultipartFile studentFile) {
        HashMap<Integer, List<Integer>> colors = new HashMap<>();
        List<Integer> checkIfContainsGivenCRN = new ArrayList<>();
        int colorNumber = 0;

        for (Integer crnA : subjects.keySet()) {
            boolean addedToExistingColor = false;

            for (Integer color : colors.keySet()) {
                List<Integer> CRN = colors.get(color);
                boolean canAddToColor = true;

                for (Integer crn : CRN) {
                    if (!AllocateRoomInGivenTimeAndDay.CompareTwoCRN(crn, crnA,studentFile)) {
                        canAddToColor = false;
                        break;
                    }
                }

                if (canAddToColor) {
                    CRN.add(crnA);
                    colors.replace(color, CRN);
                    checkIfContainsGivenCRN.add(crnA);
                    addedToExistingColor = true;
                    break;
                }
            }

            if (!addedToExistingColor) {
                List<Integer> newColorList = new ArrayList<>();
                newColorList.add(crnA);
                colors.put(colorNumber++, newColorList);
                checkIfContainsGivenCRN.add(crnA);
            }
        }

        return colors;
    }
}