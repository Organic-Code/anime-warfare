////////////////////////////////////////////////////////////
//
// Anime Warfare
// Copyright (C) 2016 TiWinDeTea - contact@tiwindetea.org
//
// This software is provided 'as-is', without any express or implied warranty.
// In no event will the authors be held liable for any damages arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it freely,
// subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented;
//    you must not claim that you wrote the original software.
//    If you use this software in a product, an acknowledgment
//    in the product documentation would be appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such,
//    and must not be misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//
////////////////////////////////////////////////////////////

package org.tiwindetea.animewarfare.gui.game;

import com.esotericsoftware.minlog.Log;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Pair;
import org.lomadriel.lfc.event.EventDispatcher;
import org.tiwindetea.animewarfare.gui.event.ZoneClickedEvent;
import org.tiwindetea.animewarfare.logic.GameMap;
import org.tiwindetea.animewarfare.net.networkevent.BattleNetevent;
import org.tiwindetea.animewarfare.net.networkevent.BattleNeteventListener;
import org.tiwindetea.animewarfare.net.networkevent.StudioNetevent;
import org.tiwindetea.animewarfare.net.networkevent.StudioNeteventListener;
import org.tiwindetea.animewarfare.net.networkevent.UnitMoveNetevent;
import org.tiwindetea.animewarfare.net.networkevent.UnitNeteventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author Lucas Lazare
 * @since 0.1.0
 */
public class GMap extends Pane implements UnitNeteventListener, StudioNeteventListener, BattleNeteventListener {

    // TODO: rework this class

    private final ImageView MAP_PICT;
    private final Group POLYGONES = new Group();
    private final ZoneComponentsMap MAP = new ZoneComponentsMap();
    private final List<Pair<Polygon, Tooltip>> ZONES = new ArrayList<>(17);
    private boolean isDisplayingZonesGrids = false;
    private boolean isDisplayinGComponentsGrids = false;

    public GMap() {
        super();
        Image pic = new Image(this.getClass().getClassLoader().getResource("org/tiwindetea/animewarfare/gui/game/pictures/map.png").toString());
        this.MAP_PICT = new ImageView(pic);
        initZones();
        getChildren().addAll(this.MAP_PICT, this.POLYGONES, this.MAP);
        this.MAP_PICT.autosize();
        this.POLYGONES.autosize();
        this.MAP.autosize();

        this.setOnScroll(e -> {
            setScaleX(getScaleX() + getScaleX() * e.getDeltaY() / 1000.0); // TODO externalize & settings
            setScaleY(getScaleY() + getScaleY() * e.getDeltaY() / 1000.0); // TODO externalize & settings
        });
    }

    /**
     * Adds a GComponent to the map, using Javafx's thread.
     */
    public void addGComponentFxThread(GComponent gComponent, int zoneID) {
        Platform.runLater(() -> addGComponent(gComponent, zoneID));
    }

    /**
     * Adds a GComponent to the map.
     */
    public void addGComponent(GComponent gComponent, int zoneID) {
        this.MAP.put(gComponent, zoneID);
        this.ZONES.get(zoneID).getValue().setText(getDescription(zoneID));
    }

    /**
     * Removes a GComponent from the map, using Javafx's thread.
     */
    public void removeGComponentFxThread(GComponent gComponent, int zoneID) {
        Platform.runLater(() -> removeGComponent(gComponent, zoneID));
    }

    /**
     * Removes a GComponent from the map.
     */
    public void removeGComponent(GComponent gComponent, int zoneID) {
        this.MAP.remove(gComponent, zoneID);
        this.ZONES.get(zoneID).getValue().setText(getDescription(zoneID));
    }

    /**
     * Moves a GComponent from a zone to another, using JavaFX's thread.
     */
    public void moveGComponentFxThread(GComponent gComponent, int source, int destination) {
        Platform.runLater(() -> moveGComponent(gComponent, source, destination));
    }

    /**
     * Moves a GComponent from a zone to another
     */
    public void moveGComponent(GComponent gComponent, int source, int destination) {
        this.MAP.move(gComponent, source, destination);
    }

    /**
     * Removes any highlightig of a given zone, unsing JavaFX's thread
     */
    public void unHighlightFxThread(int zoneID) {
        Platform.runLater(() -> unHighlight(zoneID));
    }

    /**
     * Removes any highlightig of a given zone
     */
    public void unHighlight(int zoneID) {
        setDefaultBehavior(this.ZONES.get(zoneID).getKey());
    }

    /**
     * Highlights a given zone in a given color, using JavaFX's thread
     */
    public void highLightFxThread(int zone, Color color) {
        Platform.runLater(() -> highlight(zone, color));
    }

    /**
     * Highlights a given zone in a given color
     */
    public void highlight(int zone, Color color) {
        Polygon poly = this.ZONES.get(zone).getKey();
        poly.setOnMouseEntered(e -> {
        });
        poly.setOnMouseExited(e -> {
        });
        poly.setFill(color);
    }

    /**
     * Highlights any zone at at most {@code distance} distance from a given zone, appart from the zone itself
     */
    public void highlightNeighbour(int zoneID, int distance) {
        for (Integer integer : GameMap.getZonesAtAtMostExcept(zoneID, distance)) {
            Polygon p = this.ZONES.get(integer.intValue()).getKey();
            p.setOpacity(.3);
            p.setFill(Color.ORANGE);
        }
    }

    /**
     * Remove the highlighting of any zone within {@code distance} zones of a given zone
     */
    public void unHighlightNeigbour(int zoneID, int distance) {
        for (Integer integer : GameMap.getZonesAtAtMostExcept(zoneID, distance)) {
            Polygon p = this.ZONES.get(integer.intValue()).getKey();
            p.setFill(Color.WHITE);
            p.setOpacity(0);
        }
    }

    /**
     * Displays or Hides GComponents squares (debug)
     */
    public void displayComponentssGrids(boolean b) {
        if (b) {
            if (!this.isDisplayinGComponentsGrids) {
                this.MAP.getRectangles().stream().forEach(rectangle -> rectangle.showGrid());
                this.isDisplayinGComponentsGrids = true;
            }
        } else {
            if (this.isDisplayinGComponentsGrids) {
                this.MAP.getRectangles().stream().forEach(rectangle -> rectangle.hideGrid());
                this.isDisplayinGComponentsGrids = false;
            }
        }
    }

    /**
     * Displays or Hides zones borders
     */
    public void displayZonesGrids(boolean b) {
        if (b) {
            if (!this.isDisplayingZonesGrids) {
                this.ZONES.stream().map(e -> e.getKey()).forEach(poly -> {
                    poly.setFill(Color.TRANSPARENT);
                    poly.setOpacity(1);
                    poly.setOnMouseEntered(e -> poly.setFill(Color.rgb(255, 255, 255, 0.3)));
                    poly.setOnMouseExited(e -> poly.setFill(Color.TRANSPARENT));
                });
                this.isDisplayingZonesGrids = true;
            }
        } else {
            if (this.isDisplayingZonesGrids) {
                this.ZONES.stream().map(e -> e.getKey()).forEach(poly -> setDefaultBehavior(poly));
                this.isDisplayingZonesGrids = false;
            }
        }
    }

    public void switchComponentsGridsDisplay() {
        this.displayComponentssGrids(!this.isDisplayinGComponentsGrids);
    }

    public void switchZonesGridsDisplay() {
        this.displayZonesGrids(!this.isDisplayingZonesGrids);
    }

    /**
     * Used to disable the ScrollPane wheel scroll without removing the zooming ability
     */
    public void scrollEvent(ScrollEvent e) {
        this.getOnScroll().handle(e);
    }

    private class GComponentRectangle extends Parent {

        final double x, y;
        static final int WIDTH = 42;
        static final int HEIGHT = 42;
        final Polygon grid;

        private GComponent gComponent;

        public GComponentRectangle(double x, double y) {
            this.x = x;
            this.y = y;

            this.grid = new Polygon(x, y, x, y + WIDTH, x + WIDTH, y + WIDTH, x + WIDTH, y);
            this.grid.setFill(Color.TRANSPARENT);
            this.grid.setStroke(Color.YELLOW);
            this.grid.setMouseTransparent(true);
        }

        public void setUnit(GComponent gComponent) {
            if (this.gComponent != null) {
                this.getChildren().remove(this.gComponent);
            }
            if (gComponent != null) {
                gComponent.setTranslateX(this.x + WIDTH / 2);
                gComponent.setTranslateY(this.y + HEIGHT / 2);
                this.getChildren().add(gComponent);
            }
            this.gComponent = gComponent;
        }

        public GComponent getUnit() {
            return this.gComponent;
        }

        public void showGrid() {
            getChildren().add(this.grid);
        }

        public void hideGrid() {
            getChildren().remove(this.grid);
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof GComponentRectangle) && this.equals((GComponentRectangle) o);
        }

        public boolean equals(GComponentRectangle r) {
            return r.x == this.x && r.y == this.y;
        }

        @Override
        public int hashCode() {
            return (int) (this.x + this.y);
        }
    }

    private class ZoneComponentsMap extends Parent {

        private class Int {
            int value = 0;
        }

        private final HashMap<Integer, List<GComponentRectangle>> map = new HashMap<>();
        private final HashMap<Integer, List<GComponent>> orphans = new HashMap<>();

        private final List<Int> numberOfgComponentsPerZone = new ArrayList<>(17);

        public ZoneComponentsMap() {
            for (int i = 0; i < 17; i++) {
                this.numberOfgComponentsPerZone.add(new Int());
            }
        }

        void setZones(List<Polygon> polygons) {

            if (this.map.size() != 0) {
                throw new IllegalStateException();
            }

            int i = 0;

            for (Polygon polygon : polygons) {
                List<GComponentRectangle> rectList = new ArrayList<>(20);
                Bounds bounds = polygon.getLayoutBounds();

                int xdirection = 1;
                boolean pouredOne = false;

                for (double y = bounds.getMinY() + 3; y < bounds.getMaxY(); y += GComponentRectangle.HEIGHT) {
                    double x = xdirection > 0 ? bounds.getMinX() + 3 : bounds.getMaxX() - 3;
                    while (x > bounds.getMinX() && x < bounds.getMaxX()) {

                        do {
                            if (polygon.contains(x - 2, y - 2)
                                    && polygon.contains(x + GComponentRectangle.WIDTH + 2, y - 2)
                                    && polygon.contains(x - 2, y + GComponentRectangle.HEIGHT + 2)
                                    && polygon.contains(x + GComponentRectangle.WIDTH + 2, y + GComponentRectangle.HEIGHT + 2)
                                    ) {
                                rectList.add(new GComponentRectangle(x, y));
                                pouredOne = true;
                            }
                            x += xdirection;
                        } while (!pouredOne && x < bounds.getMaxX() && x > bounds.getMinX());
                        x += (GComponentRectangle.WIDTH - 1) * xdirection;
                    }
                    pouredOne = false;
                    xdirection = -xdirection;
                }
                getChildren().addAll(rectList);

                this.map.put(new Integer(i), rectList);
                ++i;
            }
        }

        public GComponentRectangle put(GComponent gComponent, int zoneID) {
            GComponentRectangle gcomponentRectangle = this.map.get(new Integer(zoneID)).parallelStream()
                    .filter(ur -> ur.gComponent == null)
                    .findAny().orElse(null);
            if (gcomponentRectangle != null) {
                gcomponentRectangle.setUnit(gComponent);
            } else {
                List<GComponent> list = this.orphans.get(new Integer(zoneID));
                if (list != null) {
                    list.add(gComponent);
                } else {
                    list = new ArrayList<>(5);
                    list.add(gComponent);
                    this.orphans.put(new Integer(zoneID), list);
                }
            }
            this.numberOfgComponentsPerZone.get(zoneID).value++;
            updateDescription(zoneID);

            return gcomponentRectangle;
        }

        public GComponentRectangle remove(GComponent gComponent, int zoneID) {
            GComponentRectangle gcomponentRectangle = this.map.get(new Integer(zoneID)).parallelStream()
                    .filter(ur -> gComponent.equals(ur.getUnit()))
                    .findAny()
                    .orElse(null);
            if (gcomponentRectangle != null) {
                GComponent newgComponent = null;
                List<GComponent> list = this.orphans.get(new Integer(zoneID));
                if (list != null) {
                    if (list.size() != 0) {
                        newgComponent = list.remove(0);
                    }
                }
                gcomponentRectangle.setUnit(newgComponent);
                --this.numberOfgComponentsPerZone.get(zoneID).value;
            } else {
                List<GComponent> list = this.orphans.get(new Integer(zoneID));
                if (list != null) {
                    if (list.remove(gComponent)) {
                        --this.numberOfgComponentsPerZone.get(zoneID).value;
                    }
                }
            }
            updateDescription(zoneID);

            return gcomponentRectangle;
        }

        public void move(GComponent gComponent, int source, int destination) {
            remove(gComponent, source);
            put(gComponent, destination);
        }

        public int getNumberOfComponents(int zoneID) {
            return this.numberOfgComponentsPerZone.get(zoneID).value;
        }

        public List<GComponentRectangle> getRectangles() {
            List<GComponentRectangle> ans = new ArrayList<>(200);
            for (List<GComponentRectangle> gcomponentRectangles : this.map.values()) {
                ans.addAll(gcomponentRectangles);
            }
            return ans;
        }
    }

    private String getDescription(int zoneID) {
        return "Number of units zone " + zoneID + ": " + this.MAP.getNumberOfComponents(zoneID);
    }

    private static void setDefaultBehavior(Polygon polygon) {
        polygon.setFill(Color.WHITE);
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(3);
        polygon.setOpacity(0);
        polygon.setPickOnBounds(false);
        polygon.setOnMouseEntered(e -> polygon.setOpacity(0.3));
        polygon.setOnMouseExited(e -> polygon.setOpacity(0));
    }

    private void updateDescription(int zoneID) {
        this.ZONES.get(zoneID).getValue().setText(getDescription(zoneID));
    }

    private void initZones() {

        List<Polygon> polygons = createPolyZone();
        for (int i = 0; i < polygons.size(); i++) {
            int magic_i = i;
            Polygon polygon = polygons.get(i);
            setDefaultBehavior(polygon);
            polygon.setOnMouseClicked(e -> EventDispatcher.send(new ZoneClickedEvent(magic_i, e)));
            Tooltip tooltip = new Tooltip(getDescription(i));
            Tooltip.install(polygon, tooltip);
            this.ZONES.add(new Pair(polygon, tooltip));
        }
        this.POLYGONES.getChildren().addAll(polygons);
        this.MAP.setZones(polygons);
    }

    private static List<Polygon> createPolyZone() {

        List<Polygon> polygons = new ArrayList<>(17);

        polygons.add(new Polygon(               // 0
                949.0, 0,
                1366.0, 0,
                1366.0, 220.0,
                1299.0, 194.0,
                1237.0, 125.0,
                1243.0, 115.0,
                1147.0, 65.0,
                993.0, 29.0,
                950.0, 3.0

        ));
        polygons.add(new Polygon(               // 1
                0, 0,
                608.0, 0,
                411.0, 126.0,
                422.0, 146.0,
                401.0, 142.0,
                330.0, 142.0,
                286.0, 171.0,
                265.0, 244.0,
                254.0, 216.0,
                225.0, 177.0,
                172.0, 166.0,
                99.0, 169.0,
                35.0, 181.0,
                0, 218.0,
                0, 0
        ));

        polygons.add(new Polygon(               // 2
                0.0, 218.0,
                35.0, 181.0,
                100.0, 166.0,
                173.0, 164.0,
                225.0, 180.0,
                253.0, 216.0,
                266.0, 240.0,
                265, 250,
                268.0, 344.0,
                280.0, 525.0,
                0, 562.0,
                0, 214.0
        ));
        polygons.add(new Polygon(               // 3
                5.0, 562.0,
                283.0, 522.0,
                283.0, 551.0,
                303.0, 576.0,
                413.0, 589.0,
                438.0, 612.0,
                438.0, 633.0,
                368.0, 673.0,
                354.0, 700.0,
                374.0, 732.0,
                445.0, 768.0,
                0, 768.0,
                0, 562.0
        ));
        polygons.add(new Polygon(               // 4
                266.0, 246.0,
                285.0, 169.0,
                332.0, 140.0,
                404.0, 144.0,
                530.0, 168.0,
                555.0, 177.0,
                540.0, 223.0,
                558.0, 283.0,
                640.0, 377.0,
                616.0, 401.0,
                575.0, 395.0,
                508.0, 372.0,
                386.0, 353.0,
                269.0, 350.0,
                266.0, 240.0
        ));
        polygons.add(new Polygon(               //  5
                414.0, 588.0,
                607.0, 572.0,
                638.0, 588.0,
                679.0, 626.0,
                706.0, 629.0,
                640.0, 768.0,
                445.0, 768.0,
                377.0, 731.0,
                357.0, 700.0,
                368.0, 677.0,
                440.0, 635.0,
                438.0, 614.0,
                416.0, 588.0

        ));
        polygons.add(new Polygon(               // 6
                812.0, 357.0,
                880.0, 387.0,
                950.0, 397.0,
                1011.0, 363.0,
                1040.0, 395.0,
                1106.0, 471.0,
                1119.0, 518.0,
                1098.0, 558.0,
                933.0, 641.0,
                909.0, 663.0,
                874.0, 640.0,
                892.0, 605.0,
                894.0, 531.0,
                879.0, 511.0,
                864.0, 444.0,
                831.0, 391.0,
                807.0, 376.0
        ));
        polygons.add(new Polygon(               // 7
                1202.0, 519.0,
                1368.0, 515.0,
                1368.0, 768.0,
                1173.0, 768.0,
                1186.0, 658.0,
                1183.0, 581.0
        ));
        polygons.add(new Polygon(               // 8
                1208.0, 160.0,
                1238.0, 127.0,
                1300.0, 193.0,
                1366.0, 219.0,
                1366.0, 515.0,
                1200.0, 519.0,
                1208.0, 163.0

        ));
        polygons.add(new Polygon(               // 9
                270.0, 349.0,
                391.0, 350.0,
                508.0, 370.0,
                577.0, 398.0,
                619.0, 402.0,
                636.0, 376.0,
                679.0, 425.0,
                700.0, 497.0,
                704.0, 507.0,
                636.0, 586.0,
                608.0, 572.0,
                416.0, 588.0,
                305.0, 579.0,
                283.0, 553.0
        ));
        polygons.add(new Polygon(               // 10
                555.0, 179.0,
                627.0, 194.0,
                785.0, 301.0,
                815.0, 360.0,
                807.0, 375.0,
                830.0, 391.0,
                860.0, 441.0,
                881.0, 515.0,
                894.0, 529.0,
                893.0, 605.0,
                893.0, 603.0,
                847.0, 554.0,
                762.0, 575.0,
                706.0, 629.0,
                681.0, 625.0,
                638.0, 584.0,
                706.0, 506.0,
                678.0, 428.0,
                637.0, 378.0,
                555.0, 275.0,
                538.0, 222.0
        ));
        polygons.add(new Polygon(               // 11
                706.0, 629.0,
                762.0, 575.0,
                847.0, 554.0,
                893.0, 603.0,
                875.0, 637.0,
                877.0, 641.0,
                920.0, 669.0,
                944.0, 696.0,
                967.0, 744.0,
                964.0, 768.0,
                642.0, 768.0
        ));
        polygons.add(new Polygon(               // 12
                912.0, 658.0,
                930.0, 640.0,
                1098, 558.0,
                1118.0, 521.0,
                1202.0, 519.0,
                1183.0, 581.0,
                1185.0, 660.0,
                1173.0, 768.0,
                963.0, 768.0,
                967.0, 748.0,
                944.0, 696.0,
                922.0, 670.0,
                909.0, 660.0
        ));
        polygons.add(new Polygon(               // 13
                1006.0, 239.0,
                1053.0, 223.0,
                1202.0, 287.0,
                1199.0, 518.0,
                1117.0, 520.0,
                1104.0, 473.0,
                1041.0, 395.0,
                1011.0, 363.0,
                1032.0, 285.0,
                1012.0, 243.0
        ));
        polygons.add(new Polygon(               // 14
                770.0, 161.0,
                949.0, 80.0,
                988.0, 26.0,
                1146.0, 64.0,
                1246.0, 116.0,
                1210.0, 156.0,
                1203.0, 286.0,
                1053.0, 227.0,
                1009.0, 242.0,
                975.0, 216.0,
                955.0, 207.0,
                916.0, 201.0,
                829.0, 210.0,
                775.0, 188.0
        ));
        polygons.add(new Polygon(               // 15
                559.0, 174.0,
                636.0, 94.0,
                676.0, 117.0,
                742.0, 125.0,
                770.0, 154.0,
                775.0, 188.0,
                826.0, 210.0,
                915.0, 203.0,
                953.0, 207.0,
                975.0, 214.0,
                1002.0, 237.0,
                1012.0, 243.0,
                1033.0, 283.0,
                1011.0, 363.0,
                950.0, 397.0,
                879.0, 387.0,
                812.0, 356.0,
                785.0, 301.0,
                625.0, 194.0,
                557.0, 177.0
        ));
        polygons.add(new Polygon(               // 16
                608.0, 0,
                947.0, 0,
                987.0, 25.0,
                947.0, 82.0,
                772.0, 165.0,
                768.0, 152.0,
                744.0, 125.0,
                682.0, 119.0,
                635.0, 93.0,
                555.0, 176.0,
                422.0, 146.0,
                411.0, 126.0,
                606.0, 0.0
        ));
        return polygons;
    }


    @Override
    public void handleUnitMovedNetevent(UnitMoveNetevent event) {
        if (event.getDestination() != Integer.MIN_VALUE) {
            if (event.getSource() != Integer.MIN_VALUE) {
                moveGComponentFxThread(GUnit.get(event.getUnitID()), event.getSource(), event.getDestination());
            } else {
                addGComponentFxThread(GUnit.get(event.getUnitID()), event.getDestination());
            }
        } else if (event.getSource() != Integer.MIN_VALUE) {
            removeGComponentFxThread(GUnit.get(event.getUnitID()), event.getSource());
        } else {
            Log.warn("Received " + UnitMoveNetevent.class.getName().toString() + " with both destination and target and 0.");
        }
    }

    @Override
    public void handleStudioCreation(StudioNetevent event) {
        this.addGComponentFxThread(GStudio.getOrCreate(event.getZoneID()), event.getZoneID());
    }

    @Override
    public void handleStudioRemoved(StudioNetevent event) {
        this.removeGComponentFxThread(GStudio.get(event.getZoneID()), event.getZoneID());
    }

    @Override
    public void handleStudioDeserted(StudioNetevent event) {
        Platform.runLater(() -> GStudio.get(event.getZoneID()).setTeam(null));
    }

    @Override
    public void handleStudioCaptured(StudioNetevent event) {
        Platform.runLater(() -> GStudio.get(event.getZoneID()).setTeam(event.getPlayerID()));
    }

    @Override
    public void handlePreBattle(BattleNetevent event) {
        this.highLightFxThread(event.getZone(), Color.color(1.0f, 0.64705884f, 0.0f, 0.3));
    }

    @Override
    public void handleDuringBattle(BattleNetevent event) {
        // todo
    }

    @Override
    public void handlePostBattle(BattleNetevent event) {
        // todo
    }

    @Override
    public void handleBattleFinished(BattleNetevent event) {
        this.unHighlightFxThread(event.getZone());
    }
}