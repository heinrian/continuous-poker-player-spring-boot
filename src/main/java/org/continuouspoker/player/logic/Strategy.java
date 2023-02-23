package org.continuouspoker.player.logic;

import org.continuouspoker.player.model.Bet;
import org.continuouspoker.player.model.Card;
import org.continuouspoker.player.model.Player;
import org.continuouspoker.player.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Strategy {

    public Bet decide(final Table table) {
        Bet bet = new Bet();

        Player own = table.getPlayers().get(table.getActivePlayer());

        List<Card> cardsTable = table.getCommunityCards();
        List<Card> ourCards = own.getCards();
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(cardsTable);
        allCards.addAll(ourCards);

        Map<String, Integer> rankCount = new HashMap<>();
        allCards.stream()
                .map(card -> card.getRank().getValue()).collect(Collectors.toList()).forEach(rank -> {
                    int count = 0;
                    if (rankCount.containsKey(rank)) {
                        count = rankCount.get(rank);
                    }
                    rankCount.put(rank, ++count);
                });

        List<String> ownRank = ourCards.stream().map(card -> card.getRank().getValue()).collect(Collectors.toList());

        if (rankCount.containsValue(4) &&
                keys(rankCount, 4).collect(Collectors.toList()).contains(ownRank.get(0)) ||
                keys(rankCount, 4).collect(Collectors.toList()).contains(ownRank.get(1))
        ) {
            bet.bet(own.getStack());
        } else if (rankCount.containsValue(2) &&
                keys(rankCount, 2).collect(Collectors.toList()).contains(ownRank.get(0)) ||
                keys(rankCount, 2).collect(Collectors.toList()).contains(ownRank.get(1))) {
            bet.bet(own.getStack() / 2);
        } else {
            bet.bet(0);
        }


        return bet;
    }


    public <K, V> Stream<K> keys(Map<K, V> map, V value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);
    }

}
