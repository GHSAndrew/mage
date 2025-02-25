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
package mage.sets.magic2012;

import java.util.UUID;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousRuleModifyingEffectImpl;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 * @author nantuko
 */
public class GrandAbolisher extends CardImpl {

    public GrandAbolisher(UUID ownerId) {
        super(ownerId, 19, "Grand Abolisher", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{W}{W}");
        this.expansionSetCode = "M12";
        this.subtype.add("Human");
        this.subtype.add("Cleric");

        this.color.setWhite(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // During your turn, your opponents can't cast spells or activate abilities of artifacts, creatures, or enchantments.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new GrandAbolisherEffect()));
    }

    public GrandAbolisher(final GrandAbolisher card) {
        super(card);
    }

    @Override
    public GrandAbolisher copy() {
        return new GrandAbolisher(this);
    }
}

class GrandAbolisherEffect extends ContinuousRuleModifyingEffectImpl {

    public GrandAbolisherEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "During your turn, your opponents can't cast spells or activate abilities of artifacts, creatures, or enchantments";        
    }

    public GrandAbolisherEffect(final GrandAbolisherEffect effect) {
        super(effect);        
    }

    @Override
    public GrandAbolisherEffect copy() {
        return new GrandAbolisherEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public String getInfoMessage(Ability source, GameEvent event, Game game) {
        Player activePlayer = game.getPlayer(game.getActivePlayerId());
        MageObject mageObject = game.getObject(source.getSourceId());
        if (activePlayer != null && mageObject != null) {
            return "You can't cast spells or activate abilities of artifacts, creatures, or enchantments during the turns of " + activePlayer.getLogName() +
                    " (" + mageObject.getLogName() + ")";
        }
        return null;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.CAST_SPELL || event.getType() == GameEvent.EventType.ACTIVATE_ABILITY;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (game.getActivePlayerId().equals(source.getControllerId()) && game.getOpponents(source.getControllerId()).contains(event.getPlayerId())) {
            switch(event.getType()) {
                case CAST_SPELL:
                    return true;
                case ACTIVATE_ABILITY:
                    Permanent permanent = game.getPermanent(event.getSourceId());
                    if (permanent != null) {
                        return permanent.getCardType().contains(CardType.ARTIFACT) || permanent.getCardType().contains(CardType.CREATURE)
                                || permanent.getCardType().contains(CardType.ENCHANTMENT);
                    }            
            }   
        }
        return false;
    }
}
