package com.example.dividend.model.constants;

public enum Month {

    JAN("Jan", 1),
    FEB("Feb", 2),
    MAR("Mar", 3),
    APR("Apr", 4),
    MAY("May", 5),
    JUN("Jun", 6),
    JUL("Jul", 7),
    AUG("Aug", 8),
    SEP("Sep", 9),
    OCT("Oct", 10),
    NOV("Nov", 11),
    DEC("Dec", 12)
    ;

    private String s;
    private int number;

    Month(String s, int number) {
        this.s = s;
        this.number = number;
    }

    // 해당 월의 문자열 -> 숫자로 변환해 반환
    public static int strToNumber(String s) {
        for(Month month : Month.values()) {
            if(month.s.equals(s)) {
                return month.number;
            }
        }
        return -1;
    }
}
