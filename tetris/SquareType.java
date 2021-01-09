package se.liu.ida.dinadress.tddd78.tetris;

import java.util.Random;

public enum SquareType
{
    EMPTY, I, O, T, S, Z, J, L, OUTSIDE;
    public static  void main(String[] args){
        final SquareType[] myArray = SquareType.values();
        Random rnd = new Random();
        for (int i = 0; i<=25; i++){
             int index = rnd.nextInt(myArray.length);
             System.out.println(myArray[index]);
    }

    }
}



