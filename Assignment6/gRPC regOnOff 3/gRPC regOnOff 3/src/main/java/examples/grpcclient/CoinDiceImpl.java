package examples.grpcclient;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import service.*;
import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;

import javax.swing.plaf.PanelUI;

public class CoinDiceImpl extends CoinDiceGrpc.CoinDiceImplBase {

    @Override
    public void flipCoin(CoinFlipRequest request, StreamObserver<CoinFlipResponse> responseObserver) {


    }

    @Override
    public void rollDice(DiceRollRequest request, StreamObserver<DiceRollResponse> responseObserver) {

    }
}
