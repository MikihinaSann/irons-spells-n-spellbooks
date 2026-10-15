package net.acetheeldritchking.aces_spell_utils.utils.boss_music;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericBossEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;

@EventBusSubscriber({Dist.CLIENT})
public class BossMusicManager {
   @Nullable
   private static BossMusicManager INSTANCE;
   static final SoundSource SOUND_SOURCE = SoundSource.RECORDS;
   GenericBossEntity genericBoss;
   final SoundManager manager;
   BossSoundInstance bossMusic;
   BossSoundInstance bossTransitionMusic;
   BossSoundInstance bossAltMusic;
   int phaseForTransition;
   int phaseForMusicChange;
   boolean hasCustomMusic;
   GenericBossEntity.Phase phase;
   Set<BossSoundInstance> layers = new HashSet<>();
   boolean finishedPlaying = false;

   private BossMusicManager(GenericBossEntity boss) {
      this.genericBoss = boss;
      this.manager = Minecraft.getInstance().getSoundManager();
      this.phase = GenericBossEntity.Phase.values()[boss.getPhase()];
      this.phaseForTransition = boss.usePhaseAsTransition();
      this.phaseForMusicChange = boss.usePhaseForMusicChange();
      this.hasCustomMusic = boss.hasCustomMusic();
      this.bossMusic = new BossSoundInstance(this.getBossMusic(), SOUND_SOURCE, true);
      this.bossTransitionMusic = new BossSoundInstance(this.getTransitionMusic(), SOUND_SOURCE, true);
      this.bossAltMusic = new BossSoundInstance(this.getOtherPhaseMusic(), SOUND_SOURCE, true);
      this.init();
   }

   private void init() {
      this.manager.stop(null, SoundSource.MUSIC);
      if (this.hasCustomMusic) {
         switch (this.phase) {
            case FirstPhase:
               this.addLayer(this.bossMusic);
               break;
            case SecondPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 1) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 1) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case ThirdPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 2) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 2) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case FourthPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 3) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 3) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case FifthPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 4) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 4) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case SixthPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 5) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 5) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case SeventhPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 6) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 6) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case EighthPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 7) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 7) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case NinethPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 8) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 8) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case TenthPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 9) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 9) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case EleventhPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 10) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 10) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            case TwelfthPhase:
               if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && this.genericBoss.usePhaseAsTransition() == 11) {
                  this.addLayer(this.bossTransitionMusic);
               } else if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.usePhaseForMusicChange() == 11) {
                  this.addLayer(this.bossAltMusic);
               }
               break;
            default:
               if (!this.genericBoss.changeMusicOnPhaseChange()) {
                  this.addLayer(this.bossMusic);
               }
         }
      }
   }

   public SoundEvent getBossMusic() {
      return this.genericBoss.getBossMusic();
   }

   public SoundEvent getTransitionMusic() {
      return this.genericBoss.getTransitionMusic();
   }

   public SoundEvent getOtherPhaseMusic() {
      return this.genericBoss.getOtherPhaseMusic();
   }

   @SubscribeEvent
   public static void clientTick(Pre event) {
      if (INSTANCE != null && !Minecraft.getInstance().isPaused()) {
         INSTANCE.tick();
      }
   }

   public static void createOrResumeInstance(GenericBossEntity boss) {
      if (INSTANCE != null && !INSTANCE.isDonePlaying()) {
         INSTANCE.triggerResumeMusic(boss);
      } else {
         INSTANCE = new BossMusicManager(boss);
      }
   }

   public static void stop(GenericBossEntity boss) {
      if (INSTANCE != null && INSTANCE.genericBoss.getUUID().equals(boss.getUUID())) {
         INSTANCE.stopLayers();
         INSTANCE.finishedPlaying = true;
      }
   }

   private void tick() {
      if (!this.isDonePlaying() && !this.finishedPlaying) {
         if (!this.genericBoss.isDeadOrDying() && !this.genericBoss.isRemoved()) {
            GenericBossEntity.Phase bossPhase = GenericBossEntity.Phase.values()[this.genericBoss.getPhase()];
            int transitionPhase = this.genericBoss.usePhaseAsTransition();
            int changePhase = this.genericBoss.usePhaseForMusicChange();
            if (this.genericBoss.hasCustomMusic()) {
               switch (bossPhase) {
                  case FirstPhase:
                     if (!this.manager.isActive(this.bossMusic)) {
                        this.playFirstPhaseMusic();
                     }
                     break;
                  case SecondPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 1) {
                        if (this.phase != GenericBossEntity.Phase.SecondPhase) {
                           this.phase = GenericBossEntity.Phase.SecondPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 1 && this.phase != GenericBossEntity.Phase.SecondPhase) {
                        this.phase = GenericBossEntity.Phase.SecondPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case ThirdPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 2) {
                        if (this.phase != GenericBossEntity.Phase.ThirdPhase) {
                           this.phase = GenericBossEntity.Phase.ThirdPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 2 && this.phase != GenericBossEntity.Phase.ThirdPhase) {
                        this.phase = GenericBossEntity.Phase.ThirdPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case FourthPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 3) {
                        if (this.phase != GenericBossEntity.Phase.FourthPhase) {
                           this.phase = GenericBossEntity.Phase.FourthPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 3 && this.phase != GenericBossEntity.Phase.FourthPhase) {
                        this.phase = GenericBossEntity.Phase.FourthPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case FifthPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 4) {
                        if (this.phase != GenericBossEntity.Phase.FifthPhase) {
                           this.phase = GenericBossEntity.Phase.FifthPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 4 && this.phase != GenericBossEntity.Phase.FifthPhase) {
                        this.phase = GenericBossEntity.Phase.FifthPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case SixthPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 5) {
                        if (this.phase != GenericBossEntity.Phase.SixthPhase) {
                           this.phase = GenericBossEntity.Phase.SixthPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 5 && this.phase != GenericBossEntity.Phase.SixthPhase) {
                        this.phase = GenericBossEntity.Phase.SixthPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case SeventhPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 6) {
                        if (this.phase != GenericBossEntity.Phase.SeventhPhase) {
                           this.phase = GenericBossEntity.Phase.SeventhPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 6 && this.phase != GenericBossEntity.Phase.SeventhPhase) {
                        this.phase = GenericBossEntity.Phase.SeventhPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case EighthPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 7) {
                        if (this.phase != GenericBossEntity.Phase.EighthPhase) {
                           this.phase = GenericBossEntity.Phase.EighthPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 7 && this.phase != GenericBossEntity.Phase.EighthPhase) {
                        this.phase = GenericBossEntity.Phase.EighthPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case NinethPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 8) {
                        if (this.phase != GenericBossEntity.Phase.NinethPhase) {
                           this.phase = GenericBossEntity.Phase.NinethPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 8 && this.phase != GenericBossEntity.Phase.NinethPhase) {
                        this.phase = GenericBossEntity.Phase.NinethPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case TenthPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 9) {
                        if (this.phase != GenericBossEntity.Phase.TenthPhase) {
                           this.phase = GenericBossEntity.Phase.TenthPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 9 && this.phase != GenericBossEntity.Phase.TenthPhase) {
                        this.phase = GenericBossEntity.Phase.TenthPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case EleventhPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 10) {
                        if (this.phase != GenericBossEntity.Phase.EleventhPhase) {
                           this.phase = GenericBossEntity.Phase.EleventhPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 10 && this.phase != GenericBossEntity.Phase.EleventhPhase) {
                        this.phase = GenericBossEntity.Phase.EleventhPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  case TwelfthPhase:
                     if (this.genericBoss.changeMusicOnPhaseChange() && this.genericBoss.hasTransitionPhase() && transitionPhase == 11) {
                        if (this.phase != GenericBossEntity.Phase.TwelfthPhase) {
                           this.phase = GenericBossEntity.Phase.TwelfthPhase;
                           this.stopLayers();
                           this.playTransitionPhaseMusic();
                        }
                     } else if (this.genericBoss.changeMusicOnPhaseChange() && changePhase == 11 && this.phase != GenericBossEntity.Phase.TwelfthPhase) {
                        this.phase = GenericBossEntity.Phase.TwelfthPhase;
                        this.stopLayers();
                        this.playAltPhaseMusic();
                     }
                     break;
                  default:
                     if (!this.manager.isActive(this.bossMusic) && !this.genericBoss.changeMusicOnPhaseChange()) {
                        this.playFirstPhaseMusic();
                     }
               }
            }
         } else {
            this.stopLayers();
            this.finishedPlaying = true;
         }
      }
   }

   private boolean isDonePlaying() {
      for (BossSoundInstance soundInstance : this.layers) {
         if (!soundInstance.isStopped() && this.manager.isActive(soundInstance)) {
            return false;
         }
      }

      return true;
   }

   private void addLayer(BossSoundInstance instance) {
      this.layers.stream().filter(sound -> sound.isStopped() || !this.manager.isActive(sound)).toList().forEach(this.layers::remove);
      this.manager.play(instance);
      this.layers.add(instance);
   }

   public void stopLayers() {
      this.layers.forEach(BossSoundInstance::triggerStop);
   }

   public static void hardStop() {
      if (INSTANCE != null) {
         INSTANCE.layers.forEach(INSTANCE.manager::stop);
         INSTANCE = null;
      }
   }

   public void triggerResumeMusic(GenericBossEntity boss) {
      if (boss.getUUID().equals(this.genericBoss.getUUID())) {
         this.genericBoss = boss;
      }

      if (this.genericBoss.isRemoved()) {
         this.layers.forEach(sound -> {
            if (!this.manager.isActive(sound)) {
               this.manager.play(sound);
            }
         });
         this.finishedPlaying = false;
      }
   }

   private void playFirstPhaseMusic() {
      this.addLayer(this.bossMusic);
   }

   private void playTransitionPhaseMusic() {
      this.addLayer(this.bossTransitionMusic);
   }

   private void playAltPhaseMusic() {
      this.addLayer(this.bossAltMusic);
   }
}
