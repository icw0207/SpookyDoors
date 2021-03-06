package com.example.spookydoors;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

class Dice
{
    private static Random rollingNumber = new Random(); //주사위 객체를 만들어두어요

    //a면체 주사위를 굴려요 1~a
    public static int rollingDice(int a)
    {
        return rollingNumber.nextInt(a) + 1;
    }
}

class ChaRacter
{
    protected int xCoordinate; //x = 0~6 사이의 값을 가짐.
    protected int yCoordinate; //y = 0~3
    protected int candy; //소지 캔디 갯수
    protected boolean isItAttacked; //해당 라운드에 공격을 당했는지 안당했는지 표시하는 속성. 한 턴에 한번만 공격 당할 수 있다.

    //생성자
    public ChaRacter(int x, int y)
    {
        xCoordinate = x;
        yCoordinate = y;
        candy = 3; //기본 캔디는 항상 3개
        isItAttacked = false;
    }

    //이번 턴에 타인과 같은 칸에 위치하게 되어 공격 당했을 때
    public void attacked()
    {
        //아직 공격당한 적이 없으면서 캔디를 소지하고 있을 때
        if(!isItAttacked && candy > 0)
        {
            isItAttacked = true; //공격당했음을 표시
            candy --; //캔디 1개 감소
        }
    }

    public void earnCandy()
    {
        candy ++;
    }

    public void resetAttacked()
    {
        isItAttacked = false;
    }


    //속성을 return 하는 함수들
    public int getX()
    {
        return xCoordinate;
    }
    public int getY()
    {
        return yCoordinate;
    }
    public int getCandy()
    {
        return candy;
    }
    public boolean getIsItAttacked()
    {
        return isItAttacked;
    }
    public void testPrint()
    {
        //System.out.printf("이 캐릭터는 (%d, %d)에 위치하고 있으며, %d개의 캔디를 갖고 있습니다.\n", xCoordinate, yCoordinate, candy);
        Log.e("testPrint npc", String.valueOf(xCoordinate) + String.valueOf(yCoordinate) + String.valueOf(candy));
    }
}

class NounPlayerCharacter extends ChaRacter
{
    public NounPlayerCharacter(int x, int y)
    {
        super(x, y);
        //상위 클래스 생성자 candy = 3;정의한 것 등도 잘 들어간 것 확인하였음.
    }

    public void randomMove()
    {
        //x를 -1~1칸 랜덤하게 움직이도록 함.
        while(true)
        {
            //3면체 주사위를 굴리고 2를 빼서 -1, 0, 1의 결과값을 가진다
            int dice = (Dice.rollingDice(3) - 2);
            if ((xCoordinate + dice > 0) && (xCoordinate + dice < 6)) //결과값이 이동 가능 범위를 벗어나지 않는다면
            {
                xCoordinate += dice; //이동 후 종료
                break;
            }
        }
    }

}

class PlayerCharacter extends ChaRacter
{
    private int actionPoint = 0;
    //private ArrayList<Door> hasDoors; //갖고 있는 문의 종류에 대한 리스트, 처음 생성할 때 한개의 문을 갖고 시작함.

    private boolean secondRoll; //자신의 턴에서 두번째 주사위 굴림 기회가 있는지(상대방의 사탕을 먹은 뒤) 체크한다.
    private boolean firstRoll; //초기에 주사위 여러번 굴리는 것 방지. 첫번째 굴림 기회가 있으면 true로 표시한다.
    private boolean secondRolled; //두번째 주사위를 굴렸던 적이 있는지 체크한다. 한 턴에 둘 이상의 캐릭터를 공격하더라도 굴림 기회는 1회만 추가된다.

    public PlayerCharacter(int x, int y, String doorShape) //생성자
    {
        super(x, y);
        secondRoll = false;
        firstRoll = true;
        secondRolled = false;
        //hasDoors = new ArrayList<Door>();
        //hasDoors.add(new Door(x, y, doorShape)); //x, y값은 아무렇게나 넣어서 리스트에 추가해둔다.
        //문을 생성할 때는 플레이어 캐릭터의 x,y값을 받아 새 문을 설치하고, 소지 문 목록에서 삭제하기 때문.
    }

    public void rollingDice() {
        //주사위를 굴리는 함수. 함수 내에서 굴림 기회가 있는지 체크하여 AP를 갱신한다.
        if(firstRoll == true) //첫번째 굴림 기회가 있는 경우, 주사위를 굴리고 첫번째 굴림 기회를 없앤다.
        {
            actionPoint = Dice.rollingDice(6); //액션 포인트는 추가가 아니라 고정값으로 +=이 아니다.
            firstRoll = false;
        }
        else if(secondRoll == true && secondRolled == false) //2번째 굴림 기회를 얻었으면서 2번째 굴림을 한 적 없는 경우, 주사위를 굴리고 boolean을 갱신한다.
        {
            actionPoint = Dice.rollingDice(6); //액션 포인트 추가가 아니라 고정값으로 +=이 아니다.
            secondRoll = false;
            secondRolled = true;
        }
        else //굴림기회가 없는 경우
        {
            Log.e("dice error",  "굴림 기회가 없습니다.");
        }

    }

    public void moveRight() {
        if(actionPoint > 0) {
            if (xCoordinate != 6) //오른쪽 맨 끝 칸이 아닌 경우
            {
                xCoordinate++;
                actionPoint--;
            } else {
                //System.out.println("해당 방향으로 이동할 수 없습니다.");
                Log.e("move error", "해당 방향으로는 이동할 수 없습니다");
            }
        }else {
            Log.e("move error", "액션 포인트가 없어 이동할 수 없습니다.");
        }
    }

    public void moveLeft() {
        if(actionPoint > 0) {
            if (xCoordinate != 0) {
                xCoordinate--;
                actionPoint--;
            } else {
                //System.out.println("해당 방향으로 이동할 수 없습니다.");
                Log.e("move error", "해당 방향으로는 이동할 수 없습니다");
            }
        }else{
            Log.e("move error", "액션 포인트가 없어 이동할 수 없습니다.");
        }
    }

    public void useDoor(int x, int y) //문을 사용할 때 x,y를 받아서 한번에 이동한다.
    {
        if(actionPoint > 0)
        {
            //문을 사용할 때는 나왔던 문의 isitused 값이 변경되어야 한다.
            xCoordinate = x;
            yCoordinate = y;
            actionPoint--;
        }
    }

    /* 문의 회수와 설치에 관련된 함수이다.
    //문을 회수하면서 갖고 있는 문에 하나를 추가한다.
    public void addDoor(int x, int y, String doorShape) {
        if(actionPoint > 0) {
            //문을 회수할 때는 main에 있는 전체 doors관리 array에서 해당 문이 삭제되어야 한다.
            hasDoors.add(new Door(x, y, doorShape));
            actionPoint--;
        }
    }

    //index값에 해당하는 위치의 Door를 지운다
    public void deleteDoor(int index) {
        if(actionPoint > 0) {
            hasDoors.remove(index);
            actionPoint--;
        }
    }*/

    public void earnCandy()
    {
        candy++;
        if(!secondRolled)
        {
            secondRoll = true; //캔디를 얻고 두번째 주사위 굴림 기회를 얻습니다!
            actionPoint = 0; //액션 포인트를 0으로 초기화 해버림
        }
    }

    public void resetSecondRolled() {
        secondRolled = false;
        secondRoll = false;
        firstRoll = true; //한 턴에 체크하는 전체 boolean 변수들 초기화
    }


    /*
    public ArrayList<Door> getHasDoors() {
        return hasDoors;
    }*/

    //밑으로 속성 리턴받는 함수들과 테스트 프린트 함수
    public int getActionPoint() {
        return actionPoint;
    }

    public boolean getSecondRoll() {
        return secondRoll;
    }
    public boolean getFistRoll(){return firstRoll;}

    public void testPrint()
    {
        /*
        //System.out.printf("이 캐릭터는 (%d, %d)에 위치하고 있으며, %d개의 캔디를 갖고 있습니다. 남은 AP는 %d입니다.\n", xCoordinate, yCoordinate, candy, actionPoint);
        Log.e("testPrint pc", String.valueOf(xCoordinate) + String.valueOf(yCoordinate) + String.valueOf(candy) + String.valueOf(actionPoint));
        for(Door door : hasDoors)
        {
            //System.out.printf("갖고 있는 door의 스트링은 : %s\n", door.getShape());
            Log.e("doorShape", door.getShape());
        }
         */
        Log.e("testPrint pc", "순서대로 x, y, candy, ap" + String.valueOf(xCoordinate) + String.valueOf(yCoordinate) + String.valueOf(candy) + String.valueOf(actionPoint));
    }
}

class Door
{
    final private int xCoordinate;
    final private int yCoordinate;
    final private String shape;
    //private boolean isItUsed; //이것은 '나오는 문'에 대한 속성이다. 나왔던 문이 회수 가능한 지 확인하는 것.

    //생성자
    //문의 쉐이프는 "C", "W", "N", "D"
    public Door(int x, int y, String shape)
    {
        xCoordinate = x;
        yCoordinate = y;
        this.shape = shape;
        //isItUsed = false;
    }

    /* 문이 사용되었는지 확인하는 속성에 관련된 것으로, 문을 회수할 때 사용한다.
    public void useIt()
    {
        isItUsed = true;
    }
    public void resetUse()
    {
        isItUsed = false;
    }*/


    //속성을 return하는 GET함수들
    public int getX()
    {
        return xCoordinate;
    }
    public int getY()
    {
        return yCoordinate;
    }
    public String getShape()
    {
        return shape;
    }
    /*
    public boolean getIsItUsed()
    {
        return isItUsed;
    }
     */
    public void testPrint()
    {
        //System.out.printf("이 door은 (%d, %d)에 위치한 %s입니다.\n", xCoordinate, yCoordinate, shape);
        Log.e("testPrint door:", String.valueOf(xCoordinate) + String.valueOf(xCoordinate) + shape);
    }
}

public class classes
{
}
