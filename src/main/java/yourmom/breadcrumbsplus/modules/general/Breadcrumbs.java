package yourmom.breadcrumbsplus.modules.general;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class Breadcrumbs extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
            .name("color")
            .description("The color of the Breadcrumbs trail.")
            .defaultValue(new SettingColor(225, 25, 25))
            .build());

    private final Setting<SettingColor> othercolor = sgGeneral.add(new ColorSetting.Builder()
            .name("othercolor")
            .description("Color of others' trails")
            .defaultValue(new SettingColor(0, 210, 255))
            .build());

    private final Setting<Integer> maxSections = sgGeneral.add(new IntSetting.Builder()
            .name("max-sections")
            .description("The maximum number of sections.")
            .defaultValue(1000)
            .min(1)
            .sliderRange(1, 5000)
            .build());

    private final Setting<Double> sectionLength = sgGeneral.add(new DoubleSetting.Builder()
            .name("section-length")
            .description("The section length in blocks.")
            .defaultValue(0.5)
            .min(0)
            .sliderMax(1)
            .build());

    public static MinecraftClient MCInstance = MinecraftClient.getInstance();

    // HashMap<PlayerEntity,Pool<Section>> poolplrmap = new HashMap<>();
    HashMap<PlayerEntity,Queue<Vec3d>> dqplrmap = new HashMap<>();

    private DimensionType lastDimension;

    public Breadcrumbs() {
        super(Categories.Render, "Breadcrumbs+", "Displays a trail behind where you and others have walked.");
    }

    @Override
    public void onActivate() {

        lastDimension = mc.world.getDimension();
    }

    @Override
    public void onDeactivate() {
        // for (PlayerEntity v : poolplrmap.keySet()){
        //     for (Section section : dqplrmap.get(v))
        //         poolplrmap.get(v).free(section);
        // }
        dqplrmap.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (lastDimension != mc.world.getDimension()) {
            onDeactivate();
            onActivate(); // reinitalize
        }

        for (Entity player:MCInstance.world.getEntities()){
            if (player instanceof PlayerEntity){
                if (dqplrmap.containsKey(player)){
                    ArrayDeque<Vec3d> ps = (ArrayDeque<Vec3d>)dqplrmap.get((PlayerEntity)player);
                    Vec3d last = ps.getLast();
                    if (Math.sqrt(Math.pow(last.x-player.getX(),2)+Math.pow(last.z-player.getZ(),2)+Math.pow(last.y, player.getY()))>=sectionLength.get()){
                        if (ps.size() >= maxSections.get()) {
                            ps.poll();
                            // if (section != null)
                                // poolplrmap.get((PlayerEntity)player).free(section);
                        }
            
                        ps.add(player.getPos());
                    }
                } else{
                    // poolplrmap.put((PlayerEntity)player,new Pool<>(Section::new));
                    dqplrmap.put((PlayerEntity)player,new ArrayDeque<>());
                    dqplrmap.get((PlayerEntity)player).add(player.getPos());
                }
            }
        }


        lastDimension = mc.world.getDimension();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        int iLast = -1;

        for (PlayerEntity player:dqplrmap.keySet()){
            for (Vec3d section : dqplrmap.get(player)) {
                if (iLast == -1) {
                    iLast = event.renderer.lines.vec3(section.x, section.y, section.z).color(((PlayerEntity)MCInstance.player)==player?color.get():othercolor.get()).next();
                }
    
                int i = event.renderer.lines.vec3(section.x, section.y, section.z).color(((PlayerEntity)MCInstance.player)==player?color.get():othercolor.get()).next();
                event.renderer.lines.line(iLast, i);
                iLast = i;
            }
        }
    }
}