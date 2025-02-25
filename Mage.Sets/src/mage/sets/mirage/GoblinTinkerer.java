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
package mage.sets.mirage;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetArtifactPermanent;

/**
 *
 * @author LevelX2
 */
public class GoblinTinkerer extends CardImpl {

    public GoblinTinkerer(UUID ownerId) {
        super(ownerId, 180, "Goblin Tinkerer", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{1}{R}");
        this.expansionSetCode = "MIR";
        this.subtype.add("Goblin");
        this.subtype.add("Artificer");
        this.power = new MageInt(1);
        this.toughness = new MageInt(2);

        // {R}, {T}: Destroy target artifact. That artifact deals damage equal to its converted mana cost to Goblin Tinkerer.
        this.getSpellAbility().addTarget(new TargetArtifactPermanent());
        this.getSpellAbility().addEffect(new DestroyTargetEffect());
        
    }

    public GoblinTinkerer(final GoblinTinkerer card) {
        super(card);
    }

    @Override
    public GoblinTinkerer copy() {
        return new GoblinTinkerer(this);
    }
}

class GoblinTinkererDamageEffect extends OneShotEffect {
    
    public GoblinTinkererDamageEffect() {
        super(Outcome.Detriment);
        this.staticText = "That artifact deals damage equal to its converted mana cost to {this}";
    }
    
    public GoblinTinkererDamageEffect(final GoblinTinkererDamageEffect effect) {
        super(effect);
    }
    
    @Override
    public GoblinTinkererDamageEffect copy() {
        return new GoblinTinkererDamageEffect(this);
    }
    
    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Permanent targetArtifact = game.getPermanentOrLKIBattlefield(getTargetPointer().getFirst(game, source));        
        if (controller != null && targetArtifact != null) {
            Permanent sourceObject = game.getPermanent(source.getSourceId());
            int damage = targetArtifact.getManaCost().convertedManaCost();
            if (sourceObject != null && damage > 0) {
                sourceObject.damage(damage, targetArtifact.getId(), game, false, true);
            }
            return true;
        }
        return false;
    }
}
