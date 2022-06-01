package morb;

import arc.*;
import arc.assets.loaders.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.*;
import mindustry.type.*;

public class Morbustry extends Mod{
    public static StatusEffect morbing;
    public static Effect morbEffect, morbed;
    private static Sound morbSound;

    public Morbustry(){
        Events.on(FileTreeInitEvent.class, e -> {
            morbSound = new Sound();
            Core.assets.load("sounds/morbed.ogg", Sound.class, new SoundLoader.SoundParameter(morbSound));
        });
    }

    class MorbData {
        public TextureRegion region;
        public float layer;

        public MorbData(float layer, TextureRegion region){
            this.region = region;
            this.layer = layer;
        }
    }

    @Override
    public void loadContent(){
        Log.info("It's morbin' time!");

        morbEffect = new Effect(30, e -> {
            if(e.data instanceof MorbData m){
                Draw.z(m.layer - 1);
                Draw.alpha(e.fout(Interp.pow5In) * 0.8f);
                Draw.rect(m.region, e.x, e.y, e.rotation);
            }
        });

        morbed = new Effect(30, e -> {
            if(e.data instanceof Float f){
                f += 15;
                Draw.color(Pal.remove);
                Drawf.tri(e.x, e.y, f * 0.2f, f, e.rotation);
                Drawf.tri(e.x, e.y, f * 0.2f, f * 0.5f, e.rotation - 180);
            }
        });

        morbing = new StatusEffect("morbing"){{
            speedMultiplier = 3;
            healthMultiplier = 100;
            damageMultiplier = 100;
            reloadMultiplier = 5;
        }
            float timer;

            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);

                Groups.unit.intersect(unit.x - 20, unit.y - 20, 40, 40, u -> {
                    if(u.team != unit.team && !u.dead){
                        morbed.at(u.x, u.y, u.vel.angle(), u.hitSize);
                        morbSound.at(u.x, u.y);
                        u.kill();
                    }
                });

                if(timer > 1){
                    timer = 0;
                    morbEffect.at(unit.x, unit.y, unit.rotation() - 90, new MorbData(unit.type.groundLayer, unit.icon()));
                }
                timer += Time.delta;
            }
        };
    }

}
