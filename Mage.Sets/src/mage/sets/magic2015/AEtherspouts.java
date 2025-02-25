/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.magic2015;

import java.util.ArrayList;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.filter.common.FilterAttackingCreature;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentToken;
import mage.players.Player;
import mage.players.PlayerList;
import mage.target.TargetCard;

/**
 *
 * @author LevelX2
 */
public class AEtherspouts extends CardImpl {

    public AEtherspouts(UUID ownerId) {
        super(ownerId, 44, "AEtherspouts", Rarity.RARE, new CardType[]{CardType.INSTANT}, "{3}{U}{U}");
        this.expansionSetCode = "M15";

        this.color.setBlue(true);

        // For each attacking creature, its owner puts it on the top or bottom of his or her library.
        this.getSpellAbility().addEffect(new AEtherspoutsEffect());
    }

    public AEtherspouts(final AEtherspouts card) {
        super(card);
    }

    @Override
    public AEtherspouts copy() {
        return new AEtherspouts(this);
    }
}

/*
7/18/2014 	The owner of each attacking creature chooses whether to put it on the top or bottom
            of his or her library. The active player (the player whose turn it is) makes all of
            his or her choices first, followed by each other player in turn order.
7/18/2014 	If an effect puts two or more cards on the top or bottom of a library at the same time,
            the owner of those cards may arrange them in any order. That library’s owner doesn’t reveal
            the order in which the cards go into his or her library.
*/
class AEtherspoutsEffect extends OneShotEffect {

    public AEtherspoutsEffect() {
        super(Outcome.Benefit);
        this.staticText = "For each attacking creature, its owner puts it on the top or bottom of his or her library";
    }

    public AEtherspoutsEffect(final AEtherspoutsEffect effect) {
        super(effect);
    }

    @Override
    public AEtherspoutsEffect copy() {
        return new AEtherspoutsEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        game.getPlayerList();
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            PlayerList playerList = game.getPlayerList();
            playerList.setCurrent(game.getActivePlayerId());
            Player player = game.getPlayer(game.getActivePlayerId());
            Player activePlayer = player;
            do {
                ArrayList<Permanent> permanentsToTop = new ArrayList<>();
                ArrayList<Permanent> permanentsToBottom = new ArrayList<>();
                for (Permanent permanent:game.getState().getBattlefield().getActivePermanents(new FilterAttackingCreature(), player.getId(), source.getSourceId(), game)) {
                    if (permanent.getOwnerId().equals(player.getId())) {
                        if (player.chooseUse(outcome, "Put " + permanent.getLogName() + " to the top? (else it goes to bottom)", game)) {
                            permanentsToTop.add(permanent);
                            game.informPlayers(permanent.getLogName() + " goes to the top of " + player.getLogName() + "'s library");
                        } else {
                            permanentsToBottom.add(permanent);
                            game.informPlayers(permanent.getLogName() + " goes to the bottom of " + player.getLogName() + "'s library");
                        }
                    }
                }
                // cards to top
                Cards cards = new CardsImpl();
                ArrayList<Permanent> toLibrary = new ArrayList<>();
                for (Permanent permanent: permanentsToTop) {
                    if (permanent instanceof PermanentToken) {
                        toLibrary.add(permanent);
                    } else {
                        Card card = game.getCard(permanent.getId());
                        if (card != null) {
                            cards.add(card);
                        }
                    }
                }
                TargetCard target = new TargetCard(Zone.BATTLEFIELD, new FilterCard("order to put on the top of library (last choosen will be the top most)"));
                while (cards.size() > 1) {
                    if (!player.isInGame()) {
                        return false;
                    }
                    player.choose(Outcome.Neutral, cards, target, game);
                    Card card = cards.get(target.getFirstTarget(), game);
                    if (card != null) {
                        cards.remove(card);
                        Permanent permanent = game.getPermanent(card.getId());
                        if (permanent != null) {
                            toLibrary.add(permanent);
                        }
                    }
                    target.clearChosen();
                }
                if (cards.size() == 1) {
                    Card card = cards.get(cards.iterator().next(), game);
                    Permanent permanent = game.getPermanent(card.getId());
                    if (permanent != null) {
                        toLibrary.add(permanent);
                    }
                }
                // move all permanents to lib at the same time
                for(Permanent permanent: toLibrary) {
                    player.moveCardToLibraryWithInfo(permanent, source.getSourceId(), game, Zone.BATTLEFIELD, true, false);
                }
                // cards to bottom
                cards.clear();
                toLibrary.clear();
                for (Permanent permanent: permanentsToBottom) {
                    if (permanent instanceof PermanentToken) {
                        toLibrary.add(permanent);
                    } else {
                        Card card = game.getCard(permanent.getId());
                        if (card != null) {
                            cards.add(card);
                        }
                    }
                }
                target = new TargetCard(Zone.BATTLEFIELD, new FilterCard("order to put on bottom of library (last choosen will be bottommost card)"));
                while (player.isInGame() && cards.size() > 1) {
                    player.choose(Outcome.Neutral, cards, target, game);

                    Card card = cards.get(target.getFirstTarget(), game);
                    if (card != null) {
                        cards.remove(card);
                        Permanent permanent = game.getPermanent(card.getId());
                        if (permanent != null) {
                            toLibrary.add(permanent);
                        }
                    }
                    target.clearChosen();
                }
                if (cards.size() == 1) {
                    Card card = cards.get(cards.iterator().next(), game);
                    Permanent permanent = game.getPermanent(card.getId());
                    if (permanent != null) {                    
                        toLibrary.add(permanent);
                    }
                }
                // move all permanents to lib at the same time
                for(Permanent permanent: toLibrary) {
                    player.moveCardToLibraryWithInfo(permanent, source.getSourceId(), game, Zone.BATTLEFIELD, false, false);
                }
                player = playerList.getNext(game);            
            } while (player != null && !player.getId().equals(game.getActivePlayerId()) && activePlayer.isInGame());
            return true;
        }
        return false;
    }
}
