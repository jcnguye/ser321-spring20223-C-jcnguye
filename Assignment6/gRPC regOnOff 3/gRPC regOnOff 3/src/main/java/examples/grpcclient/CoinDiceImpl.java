package examples.grpcclient;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import service.*;
import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;

import javax.swing.plaf.PanelUI;
import java.util.ArrayList;
import java.util.Random;

public class CoinDiceImpl extends CoinDiceGrpc.CoinDiceImplBase {

    @Override
    public void flipCoin(CoinFlipRequest request, StreamObserver<CoinFlipResponse> responseObserver) {
        CoinFlipResponse.Builder response =  CoinFlipResponse.newBuilder();
        int numFlips = request.getNumFlips();
        String[] coinResultList = new String[numFlips];
        for(int i = 0; i < coinResultList.length; i++){
            Random random = new Random();
            int coin = random.nextInt(2);
            if(coin == 0){
                coinResultList[i] = "Tails";
            }else{
                coinResultList[i] = "Heads";
            }
        }
        for (String coin: coinResultList){
            response.addCoin(coin);
        }
        CoinFlipResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void rollDice(DiceRollRequest request, StreamObserver<DiceRollResponse> responseObserver) {
        DiceRollResponse.Builder response =  DiceRollResponse.newBuilder();

        int numRolls = request.getNumRolls();
        int numDiceSides = request.getNumSides();
        Integer[] diceRollList = new Integer[numRolls];
        for(int i = 0; i < diceRollList.length; i++){
            Random random = new Random();
            int randomDice = random.nextInt(numDiceSides + 1); // add 1 to include max value in the range
            diceRollList[i] = randomDice;
        }

        for (Integer dice: diceRollList){
            response.addDice(dice);
        }
        DiceRollResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
