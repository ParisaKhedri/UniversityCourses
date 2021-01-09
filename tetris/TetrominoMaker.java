package se.liu.ida.dinadress.tddd78.tetris;

import se.liu.ida.dinadress.tddd78.lab5.Poly;
import se.liu.ida.dinadress.tddd78.lab5.SquareType;

public class TetrominoMaker
{

    public int getNumberOfTypes() {
        return SquareType.values().length - 1;
    }

    public Poly getPoly(int n) {
        if (n > getNumberOfTypes()-1){
            throw new IllegalArgumentException("Invalid index: " + n);
        }else if(n == 0){
            return I_block();
        }else if(n == 1){
            return O_block();
        }else if(n == 2){
            return T_block();
        }else if(n == 3){
            return S_block();
        }else if(n == 4){
            return Z_block();
        }else if (n == 5){
            return J_block();
        }else if(n == 6){
            return L_block();
        }
        return null;
    }


    public Poly I_block(){
         SquareType[][] IArray = new SquareType[][]{
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},
                {SquareType.I, SquareType.I, SquareType.I, SquareType.I },
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},

         };
        return new Poly(IArray);
    }

    public Poly O_block(){
        SquareType[][] OArray = new SquareType[][]{
                {SquareType.O, SquareType.O},
                {SquareType.O, SquareType.O},
        };
        return new Poly(OArray);
    }

    public Poly T_block(){
        SquareType[][] TArray = new SquareType[][]{
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},
                {SquareType.T, SquareType.T, SquareType.T},
                {SquareType.EMPTY, SquareType.T, SquareType.EMPTY},
        };
        return new Poly(TArray);

    }

    public Poly S_block(){
        SquareType[][] SArray = new SquareType[][]{
                {SquareType.EMPTY, SquareType.S, SquareType.S},
                {SquareType.S, SquareType.S, SquareType.EMPTY},
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},

        };
        return new Poly(SArray);

    }

    public Poly Z_block(){
        SquareType[][] ZArray = new SquareType[][]{
                {SquareType.Z, SquareType.Z, SquareType.EMPTY},
                {SquareType.EMPTY, SquareType.Z, SquareType.Z},
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},
        };
        return new Poly(ZArray);
    }

    public Poly J_block(){
        SquareType[][] JArray = new SquareType[][]{
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.J},
                {SquareType.J, SquareType.J, SquareType.J},
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},
        };
        return new Poly(JArray);

    }

    public Poly L_block(){
        SquareType[][] LArray = new SquareType[][]{
                {SquareType.L, SquareType.EMPTY, SquareType.EMPTY},
                {SquareType.L, SquareType.L, SquareType.L},
                {SquareType.EMPTY, SquareType.EMPTY, SquareType.EMPTY},
        };
        return new Poly(LArray);
    }




}
