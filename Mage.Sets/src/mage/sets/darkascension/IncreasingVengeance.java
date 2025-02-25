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
package mage.sets.darkascension;

import java.util.UUID;

import mage.constants.*;
import mage.abilities.Ability;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.FlashbackAbility;
import mage.cards.CardImpl;
import mage.filter.FilterSpell;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.filter.predicate.permanent.ControllerPredicate;
import mage.game.Game;
import mage.game.stack.Spell;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetSpell;

/**
 *
 * @author BetaSteward
 */
public class IncreasingVengeance extends CardImpl {

    private static final FilterSpell filter = new FilterSpell("instant or sorcery spell");

    static {
        filter.add(Predicates.or(
                new CardTypePredicate(CardType.INSTANT),
                new CardTypePredicate(CardType.SORCERY)));
        filter.add(new ControllerPredicate(TargetController.YOU));
    }

    public IncreasingVengeance(UUID ownerId) {
        super(ownerId, 95, "Increasing Vengeance", Rarity.RARE, new CardType[]{CardType.INSTANT}, "{R}{R}");
        this.expansionSetCode = "DKA";

        this.color.setRed(true);

        // Copy target instant or sorcery spell you control. If Increasing Vengeance was cast from a graveyard, copy that spell twice instead. You may choose new targets for the copies.
        this.getSpellAbility().addEffect(new IncreasingVengeanceEffect());
        Target target = new TargetSpell(filter);
        this.getSpellAbility().addTarget(target);

        // Flashback {3}{R}{R}
        this.addAbility(new FlashbackAbility(new ManaCostsImpl("{3}{R}{R}"), TimingRule.INSTANT));
    }

    public IncreasingVengeance(final IncreasingVengeance card) {
        super(card);
    }

    @Override
    public IncreasingVengeance copy() {
        return new IncreasingVengeance(this);
    }
}

class IncreasingVengeanceEffect extends OneShotEffect {

    public IncreasingVengeanceEffect() {
        super(Outcome.BoostCreature);
        staticText = "Copy target instant or sorcery spell you control. If Increasing Vengeance was cast from a graveyard, copy that spell twice instead. You may choose new targets for the copies";
    }

    public IncreasingVengeanceEffect(final IncreasingVengeanceEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Spell spell = game.getStack().getSpell(targetPointer.getFirst(game, source));
            if (spell != null) {
                Spell copy = spell.copySpell();
                copy.setControllerId(source.getControllerId());
                copy.setCopiedSpell(true);
                game.getStack().push(copy);
                copy.chooseNewTargets(game, source.getControllerId());
                game.informPlayers(new StringBuilder(controller.getLogName()).append(copy.getActivatedMessage(game)).toString());
                Spell sourceSpell = (Spell) game.getStack().getStackObject(source.getSourceId());
                if (sourceSpell != null) {
                    if (sourceSpell.getFromZone() == Zone.GRAVEYARD) {
                        copy = spell.copySpell();
                        copy.setControllerId(source.getControllerId());
                        copy.setCopiedSpell(true);
                        game.getStack().push(copy);
                        copy.chooseNewTargets(game, source.getControllerId());
                        game.informPlayers(new StringBuilder(controller.getLogName()).append(copy.getActivatedMessage(game)).toString());
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public IncreasingVengeanceEffect copy() {
        return new IncreasingVengeanceEffect(this);
    }

}
