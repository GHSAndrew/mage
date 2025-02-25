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
package mage.sets.modernmasters;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mage.MageInt;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.common.ExileFromHandCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.GainSuspendEffect;
import mage.abilities.keyword.SuspendAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.common.FilterNonlandCard;
import mage.game.Game;
import mage.players.Player;
import mage.target.common.TargetCardInHand;

/**
 *
 * @author LevelX2
 */
public class JhoiraOfTheGhitu extends CardImpl {

    public JhoiraOfTheGhitu(UUID ownerId) {
        super(ownerId, 177, "Jhoira of the Ghitu", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{U}{R}");
        this.expansionSetCode = "MMA";
        this.supertype.add("Legendary");
        this.subtype.add("Human");
        this.subtype.add("Wizard");

        this.color.setRed(true);
        this.color.setBlue(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // {2}, Exile a nonland card from your hand: Put four time counters on the exiled card. If it doesn't have suspend, it gains suspend.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new JhoiraOfTheGhituSuspendEffect(), new GenericManaCost(2));
        ability.addCost(new ExileFromHandCost(new TargetCardInHand(new FilterNonlandCard("a nonland card from your hand"))));
        this.addAbility(ability);

    }

    public JhoiraOfTheGhitu(final JhoiraOfTheGhitu card) {
        super(card);
    }

    @Override
    public JhoiraOfTheGhitu copy() {
        return new JhoiraOfTheGhitu(this);
    }
}

class JhoiraOfTheGhituSuspendEffect extends OneShotEffect {

    public JhoiraOfTheGhituSuspendEffect() {
        super(Outcome.PutCardInPlay);
        this.staticText = "Put four time counters on the exiled card. If it doesn't have suspend, it gains suspend <i>(At the beginning of your upkeep, remove a time counter from that card. When the last is removed, cast it without paying its mana cost. If it's a creature, it has haste.)</i>";
    }

    public JhoiraOfTheGhituSuspendEffect(final JhoiraOfTheGhituSuspendEffect effect) {
        super(effect);
    }

    @Override
    public JhoiraOfTheGhituSuspendEffect copy() {
        return new JhoiraOfTheGhituSuspendEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        List<Card> cards = new ArrayList<>();
        for (Cost cost: source.getCosts()) {
            if (cost instanceof ExileFromHandCost) {
                cards = ((ExileFromHandCost) cost).getCards();
            }
        }
        if (cards != null && !cards.isEmpty()) {
            Card card = game.getCard(cards.get(0).getId());
            boolean hasSuspend = card.getAbilities().containsClass(SuspendAbility.class);

            UUID exileId = SuspendAbility.getSuspendExileId(controller.getId(), game);
            if (controller.moveCardToExileWithInfo(card, exileId, "Suspended cards of " + controller.getName(), source.getSourceId(), game, Zone.HAND, true)) {
                card.addCounters(CounterType.TIME.createInstance(4), game);
                if (!hasSuspend) {
                    game.addEffect(new GainSuspendEffect(new MageObjectReference(card, game)), source);
                }
                game.informPlayers(controller.getLogName() + " suspends 4 - " + card.getName());
                return true;
            }
        }
        return false;
    }
}

