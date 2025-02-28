package com.aiyogaguide;

public class YogaPoseChecker {

    public static String checkPose(String current, int templeftShoulderAngle, int temprightShoulderAngle, int templeftElbowAngle, int temprightElbowAngle, int templeftKneeAngle, int temprightKneeAngle, int templeftHip, int temprightHip) {
        switch (current) {
            case "Vrikshasana":
                return checkVrksasanaHandUp(templeftShoulderAngle, temprightShoulderAngle, templeftElbowAngle, temprightElbowAngle, templeftKneeAngle, temprightKneeAngle);
            case "Vrksasana (Hands Down)":
                return checkVrksasanaHandDown(templeftShoulderAngle, temprightShoulderAngle, templeftElbowAngle, temprightElbowAngle, templeftKneeAngle, temprightKneeAngle);
            case "Trikonasana":
                return checkTrikonasana(templeftElbowAngle, temprightElbowAngle, templeftKneeAngle, temprightKneeAngle, templeftHip, temprightHip);
            case "Baddha-Konasana":
                return checkBaddhaKonasana(templeftShoulderAngle, temprightShoulderAngle, templeftElbowAngle, temprightElbowAngle, templeftKneeAngle, temprightKneeAngle, templeftHip, temprightHip);
            case "Alanasana":
                return checkAlanasana(templeftKneeAngle, temprightKneeAngle, templeftShoulderAngle, temprightShoulderAngle, templeftElbowAngle, temprightElbowAngle);
            case "Virabhadrasana":
                return checkVirabhadrasana(templeftKneeAngle, temprightKneeAngle, templeftShoulderAngle, temprightShoulderAngle, templeftElbowAngle, temprightElbowAngle);
            case "Anjaneyasana":
                return checkAnjaneyasana(templeftKneeAngle, temprightKneeAngle, templeftShoulderAngle, temprightShoulderAngle, templeftElbowAngle, temprightElbowAngle);
            case "Bitilasana":
                return checkBitilasana(templeftKneeAngle, temprightKneeAngle, templeftElbowAngle, temprightElbowAngle);
            default:
                return "";
        }
    }

    static String checkVrksasanaHandUp(int templeftShoulderAngle, int temprightShoulderAngle, int templeftElbowAngle, int temprightElbowAngle, int templeftKneeAngle, int temprightKneeAngle) {
        if (!(between(templeftShoulderAngle, 16, 18) && between(temprightShoulderAngle, 16, 18))) {
            return "Incorrect shoulder angle. Please ensure your shoulder angle is between 16 and 18 degrees for both sides.";
        } else if (!(between(templeftElbowAngle, 14, 16) && between(temprightElbowAngle, 14, 16))) {
            return "Incorrect elbow angle. Please ensure your elbow angle is between 14 and 16 degrees for both sides.";
        } else if (!((between(templeftKneeAngle, 4, 6) && temprightKneeAngle > 16) || (between(temprightKneeAngle, 4, 6) && templeftKneeAngle > 16))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 4 and 6 degrees for one knee and greater than 16 degrees for the other knee.";
        }

        return "Vrikshasana";
    }

    static String checkVrksasanaHandDown(int templeftShoulderAngle, int temprightShoulderAngle, int templeftElbowAngle, int temprightElbowAngle, int templeftKneeAngle, int temprightKneeAngle) {
        if (!(between(templeftShoulderAngle, 4, 5) && between(temprightShoulderAngle, 4, 5))) {
            return "Incorrect shoulder angle. Please ensure your shoulder angle is between 4 and 5 degrees for both sides.";
        } else if (!(between(templeftElbowAngle, 4, 6) && between(temprightElbowAngle, 4, 6))) {
            return "Incorrect elbow angle. Please ensure your elbow angle is between 4 and 6 degrees for both sides.";
        } else if (!((between(templeftKneeAngle, 4, 6) && temprightKneeAngle > 16) || (between(temprightKneeAngle, 4, 6) && templeftKneeAngle > 16))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 4 and 6 degrees for one knee and greater than 16 degrees for the other knee.";
        }

        return "Vrksasana (Hands Down)";
    }

    static String checkTrikonasana(int templeftElbowAngle, int temprightElbowAngle, int templeftKneeAngle, int temprightKneeAngle, int templeftHip, int temprightHip) {
        if (!(between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 18))) {
            return "Incorrect elbow angle. Please ensure your elbow angle is between 16 and 18 degrees for both sides.";
        } else if (!((between(templeftKneeAngle, 17, 18) && between(temprightKneeAngle, 16, 18)) || (between(templeftKneeAngle, 16, 18) && between(temprightKneeAngle, 17, 18)))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 16 and 18 degrees for one knee and 17 to 18 degrees for the other knee.";
        } else if (!(between(templeftHip, 8, 10) || between(temprightHip, 8, 10))) {
            return "Incorrect hip angle. Please ensure your hip angle is between 8 and 10 degrees for at least one side.";
        }

        return "Trikonasana";
    }

    static String checkBaddhaKonasana(int templeftShoulderAngle, int temprightShoulderAngle, int templeftElbowAngle, int temprightElbowAngle, int templeftKneeAngle, int temprightKneeAngle, int templeftHip, int temprightHip) {
        if (!(between(templeftShoulderAngle, 0, 5) && between(temprightShoulderAngle, 0, 5))) {
            return "Incorrect shoulder angle. Please ensure your shoulder angle is between 0 and 5 degrees for both sides.";
        } else if (!(between(templeftElbowAngle, 17, 18) && between(temprightElbowAngle, 17, 18))) {
            return "Incorrect elbow angle. Please ensure your elbow angle is between 17 and 18 degrees for both sides.";
        } else if (!(between(templeftKneeAngle, 2, 3) && between(temprightKneeAngle, 2, 3))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 2 and 3 degrees for both sides.";
        } else if (!(between(templeftHip, 7, 8) && between(temprightHip, 7, 8))) {
            return "Incorrect hip angle. Please ensure your hip angle is between 7 and 8 degrees for both sides.";
        }

        return "Baddha-Konasana";
    }

    public static String checkAlanasana(int templeftKneeAngle, int temprightKneeAngle, int templeftShoulderAngle, int temprightShoulderAngle, int templeftElbowAngle, int temprightElbowAngle) {
        if (!(between(templeftKneeAngle, 10, 11) || between(temprightKneeAngle, 10, 11))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 10 and 11 degrees.";
        } else if (!(between(templeftKneeAngle, 16, 18) || between(temprightKneeAngle, 16, 18))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 16 and 18 degrees.";
        } else if (!(between(templeftShoulderAngle, 16, 18) && between(temprightShoulderAngle, 16, 18))) {
            return "Incorrect shoulder angle. Please ensure your shoulder angle is between 16 and 18 degrees for both sides.";
        } else if (!(between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 18))) {
            return "Incorrect elbow angle. Please ensure your elbow angle is between 16 and 18 degrees for both sides.";
        }

        return "Alanasana";
    }

    public static String checkVirabhadrasana(int templeftKneeAngle, int temprightKneeAngle, int templeftShoulderAngle, int temprightShoulderAngle, int templeftElbowAngle, int temprightElbowAngle) {
        if (!(between(templeftKneeAngle, 10, 11) || between(temprightKneeAngle, 10, 11))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 10 and 11 degrees.";
        } else if (!(between(templeftKneeAngle, 16, 18) || between(temprightKneeAngle, 16, 18))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 16 and 18 degrees.";
        } else if (!(between(templeftShoulderAngle, 9, 10) && between(temprightShoulderAngle, 9, 10))) {
            return "Incorrect shoulder angle. Please ensure your shoulder angle is between 9 and 10 degrees for both sides.";
        } else if (!(between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 18))) {
            return "Incorrect elbow angle. Please ensure your elbow angle is between 16 and 18 degrees for both sides.";
        }

        return "Virabhadrasana";
    }


    public static String checkAnjaneyasana(int templeftKneeAngle, int temprightKneeAngle, int templeftShoulderAngle, int temprightShoulderAngle, int templeftElbowAngle, int temprightElbowAngle) {
        if (!(between(templeftKneeAngle, 8, 9) || between(temprightKneeAngle, 8, 9))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 8 and 9 degrees.";
        } else if (!(between(templeftKneeAngle, 9, 11) || between(temprightKneeAngle, 9, 11))) {
            return "Incorrect knee angle. Please ensure your knee angle is between 9 and 11 degrees.";
        } else if (!(between(templeftShoulderAngle, 16, 18) && between(temprightShoulderAngle, 16, 18))) {
            return "Incorrect shoulder angle. Please ensure your shoulder angle is between 16 and 18 degrees for both sides.";
        } else if (!(between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 18))) {
            return "Incorrect elbow angle. Please ensure your elbow angle is between 16 and 18 degrees for both sides.";
        }

        return "Anjaneyasana";
    }


    public static String checkBitilasana(int templeftKneeAngle, int temprightKneeAngle, int templeftElbowAngle, int temprightElbowAngle) {
        if (between(templeftKneeAngle, 7, 8) && between(temprightKneeAngle, 7, 8)) {
            if (between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 18)) {
                return "Bitilasana";
            } else {
                if (!between(templeftElbowAngle, 16, 18)) {
                    return "Incorrect left elbow angle. Please correct your left elbow angle.";
                } else if (!between(temprightElbowAngle, 16, 18)) {
                    return "Incorrect right elbow angle. Please correct your right elbow angle.";
                }
            }
        } else {
            if (!between(templeftKneeAngle, 7, 8)) {
                return "Incorrect left knee angle. Please correct your left knee angle.";
            } else if (!between(temprightKneeAngle, 7, 8)) {
                return "Incorrect right knee angle. Please correct your right knee angle.";
            }
        }

        return "";
    }

    public static boolean between(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
