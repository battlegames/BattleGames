package dev.anhcraft.battle.utils;

import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BoundingBox {
    @NotNull
    public static BoundingBox of(@NotNull Vector v){
        return of(v, v);
    }

    @NotNull
    public static BoundingBox of(@NotNull Vector min, @NotNull Vector max){
        Condition.argNotNull("min", min);
        Condition.argNotNull("max", max);
        return new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    @NotNull
    public static BoundingBox of(@NotNull Vector v, double offsetX, double offsetY, double offsetZ){
        Condition.argNotNull("v", v);
        Condition.check(offsetX >= 0, "Offset X must not be negative");
        Condition.check(offsetY >= 0, "Offset Y must not be negative");
        Condition.check(offsetZ >= 0, "Offset Z must not be negative");
        return new BoundingBox(v.getX() - offsetX, v.getY() - offsetY, v.getZ() - offsetZ, v.getX() + offsetX, v.getY() + offsetY, v.getZ() + offsetZ);
    }

    @NotNull
    public static BoundingBox of(@NotNull Location loc){
        return of(loc, loc);
    }

    @NotNull
    public static BoundingBox of(@NotNull Location min, @NotNull Location max){
        Condition.argNotNull("min", min);
        Condition.argNotNull("max", max);
        Condition.check(Objects.equals(min.getWorld(), max.getWorld()), "Both locations must belong to the same world");
        return new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    @NotNull
    public static BoundingBox of(@NotNull Location loc, double offsetX, double offsetY, double offsetZ){
        Condition.argNotNull("loc", loc);
        Condition.check(offsetX >= 0, "Offset X must not be negative");
        Condition.check(offsetY >= 0, "Offset Y must not be negative");
        Condition.check(offsetZ >= 0, "Offset Z must not be negative");
        return new BoundingBox(loc.getX() - offsetX, loc.getY() - offsetY, loc.getZ() - offsetZ, loc.getX() + offsetX, loc.getY() + offsetY, loc.getZ() + offsetZ);
    }

    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;

    public BoundingBox() {
    }

    public BoundingBox(double x, double y, double z) {
        this.minX = x;
        this.minY = y;
        this.minZ = z;
        this.maxX = x;
        this.maxY = y;
        this.maxZ = z;
    }

    public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        allocate(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public BoundingBox(@NotNull BoundingBox box) {
        Condition.argNotNull("box", box);
        this.minX = box.minX;
        this.minY = box.minY;
        this.minZ = box.minZ;
        this.maxX = box.maxX;
        this.maxY = box.maxY;
        this.maxZ = box.maxZ;
    }

    @Contract("_, _ -> this")
    public BoundingBox allocate(@NotNull Location min, @NotNull Location max){
        Condition.argNotNull("min", min);
        Condition.argNotNull("max", max);
        Condition.check(Objects.equals(min.getWorld(), max.getWorld()), "Both locations must belong to the same world");
        return allocate(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    @Contract("_, _ -> this")
    public BoundingBox allocate(Vector min, Vector max){
        Condition.argNotNull("min", min);
        Condition.argNotNull("max", max);
        return allocate(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    @Contract("_, -> this")
    public BoundingBox allocate(@NotNull Location loc){
        Condition.argNotNull("loc", loc);
        return allocate(loc.getX(), loc.getY(), loc.getZ());
    }

    @Contract("_, -> this")
    public BoundingBox allocate(@NotNull Vector vector){
        Condition.argNotNull("vector", vector);
        return allocate(vector.getX(), vector.getY(), vector.getZ());
    }

    @Contract("_, _, _ -> this")
    public BoundingBox allocate(double x, double y, double z){
        this.minX = x;
        this.minY = y;
        this.minZ = z;
        this.maxX = x;
        this.maxY = y;
        this.maxZ = z;
        return this;
    }

    @Contract("_, _, _, _, _, _ -> this")
    public BoundingBox allocate(double minX, double minY, double minZ, double maxX, double maxY, double maxZ){
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
        this.maxZ = Math.max(maxZ, minZ);
        return this;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    @NotNull
    public Vector getMin(){
        return new Vector(minX, minY, minZ);
    }

    @NotNull
    public Vector getMax(){
        return new Vector(maxX, maxY, maxZ);
    }

    public double getLength(){
        return maxX - minX;
    }

    public double getHeight(){
        return maxY - minY;
    }

    public double getWidth(){
        return maxZ - minZ;
    }

    public double getVolume(){
        return getLength() * getHeight() * getWidth();
    }

    public double getAreaXY(){
        return getLength() * getHeight();
    }

    public double getAreaYZ(){
        return getHeight() * getWidth();
    }

    public double getAreaXZ(){
        return getLength() * getWidth();
    }

    public double getCenterX(){
        return (maxX - minX) * 0.5 + minX;
    }

    public double getCenterY(){
        return (maxY - minY) * 0.5 + minY;
    }

    public double getCenterZ(){
        return (maxZ - minZ) * 0.5 + minZ;
    }

    @NotNull
    public Vector getCenter(){
        return new Vector(getCenterX(), getCenterY(), getCenterZ());
    }

    @Contract("_, _, _ -> this")
    public BoundingBox expand(double offsetX, double offsetY, double offsetZ){
        Condition.check(offsetX >= 0, "Offset X must not be negative");
        Condition.check(offsetY >= 0, "Offset Y must not be negative");
        Condition.check(offsetZ >= 0, "Offset Z must not be negative");
        minX -= offsetX;
        minY -= offsetY;
        minZ -= offsetZ;
        maxX += offsetX;
        maxY += offsetY;
        maxZ += offsetZ;
        return this;
    }

    @Contract("_, _, _, _, _, _ -> this")
    public BoundingBox expand(double offsetMinX, double offsetMinY, double offsetMinZ, double offsetMaxX, double offsetMaxY, double offsetMaxZ){
        Condition.check(offsetMinX >= 0, "Offset min X must not be negative");
        Condition.check(offsetMinY >= 0, "Offset min Y must not be negative");
        Condition.check(offsetMinZ >= 0, "Offset min Z must not be negative");
        Condition.check(offsetMaxX >= 0, "Offset max X must not be negative");
        Condition.check(offsetMaxY >= 0, "Offset max Y must not be negative");
        Condition.check(offsetMaxZ >= 0, "Offset max Z must not be negative");
        minX -= offsetMinX;
        minY -= offsetMinY;
        minZ -= offsetMinZ;
        maxX += offsetMaxX;
        maxY += offsetMaxY;
        maxZ += offsetMaxZ;
        return this;
    }

    @Contract("_ -> this")
    public BoundingBox expand(@NotNull Vector offset){
        Condition.argNotNull("offset", offset);
        return expand(offset.getX(), offset.getY(), offset.getZ());
    }

    @Contract("_, _ -> this")
    public BoundingBox expand(@NotNull Vector offsetMin, @NotNull Vector offsetMax){
        Condition.argNotNull("offsetMin", offsetMin);
        Condition.argNotNull("offsetMax", offsetMax);
        return expand(offsetMin.getX(), offsetMin.getY(), offsetMin.getZ(), offsetMax.getX(), offsetMax.getY(), offsetMax.getZ());
    }

    @Contract("_, _, _ -> this")
    public BoundingBox shrink(double offsetX, double offsetY, double offsetZ){
        Condition.check(offsetX >= 0, "Offset X must not be negative");
        Condition.check(offsetY >= 0, "Offset Y must not be negative");
        Condition.check(offsetZ >= 0, "Offset Z must not be negative");
        double halfX = (maxX - minX) * 0.5;
        double halfY = (maxY - minY) * 0.5;
        double halfZ = (maxZ - minZ) * 0.5;
        offsetX = Math.min(offsetX, halfX);
        offsetY = Math.min(offsetY, halfY);
        offsetZ = Math.min(offsetZ, halfZ);
        minX += offsetX;
        minY += offsetY;
        minZ += offsetZ;
        maxX -= offsetX;
        maxY -= offsetY;
        maxZ -= offsetZ;
        return this;
    }

    @Contract("_, _, _, _, _, _ -> this")
    public BoundingBox shrink(double offsetMinX, double offsetMinY, double offsetMinZ, double offsetMaxX, double offsetMaxY, double offsetMaxZ){
        Condition.check(offsetMinX >= 0, "Offset min X must not be negative");
        Condition.check(offsetMinY >= 0, "Offset min Y must not be negative");
        Condition.check(offsetMinZ >= 0, "Offset min Z must not be negative");
        Condition.check(offsetMaxX >= 0, "Offset max X must not be negative");
        Condition.check(offsetMaxY >= 0, "Offset max Y must not be negative");
        Condition.check(offsetMaxZ >= 0, "Offset max Z must not be negative");
        double halfX = (maxX - minX) * 0.5;
        double halfY = (maxY - minY) * 0.5;
        double halfZ = (maxZ - minZ) * 0.5;
        minX += Math.min(halfX, offsetMinX);
        minY += Math.min(halfY, offsetMinY);
        minZ += Math.min(halfZ, offsetMinZ);
        maxX -= Math.min(halfX, offsetMaxX);
        maxY -= Math.min(halfY, offsetMaxY);
        maxZ -= Math.min(halfZ, offsetMaxZ);
        return this;
    }

    @Contract("_ -> this")
    public BoundingBox shrink(@NotNull Vector offset){
        Condition.argNotNull("offset", offset);
        return shrink(offset.getX(), offset.getY(), offset.getZ());
    }

    @Contract("_, _ -> this")
    public BoundingBox shrink(@NotNull Vector offsetMin, @NotNull Vector offsetMax){
        Condition.argNotNull("offsetMin", offsetMin);
        Condition.argNotNull("offsetMax", offsetMax);
        return shrink(offsetMin.getX(), offsetMin.getY(), offsetMin.getZ(), offsetMax.getX(), offsetMax.getY(), offsetMax.getZ());
    }

    @Contract("_ -> this")
    public BoundingBox union(@NotNull BoundingBox box){
        Condition.argNotNull("box", box);
        this.minX = Math.min(this.minX, box.minX);
        this.minY = Math.min(this.minY, box.minY);
        this.minZ = Math.min(this.minZ, box.minZ);
        this.maxX = Math.max(this.maxX, box.maxX);
        this.maxY = Math.max(this.maxY, box.maxY);
        this.maxZ = Math.max(this.maxZ, box.maxZ);
        return this;
    }

    @Contract("_, _, _ -> this")
    public BoundingBox multiply(double multiplierX, double multiplierY, double multiplierZ){
        if(multiplierX == 0) minX = maxX = 0;
        else {
            minX *= multiplierX;
            maxX *= multiplierX;
            if(multiplierX < 0) {
                minX = Math.min(minX, maxX);
                maxX = Math.max(minX, maxX);
            }
        }
        if(multiplierY == 0) minY = maxY = 0;
        else {
            minY *= multiplierY;
            maxY *= multiplierY;
            if(multiplierY < 0) {
                minY = Math.min(minY, maxY);
                maxY = Math.max(minY, maxY);
            }
        }
        if(multiplierZ == 0) minZ = maxZ = 0;
        else {
            minZ *= multiplierZ;
            maxZ *= multiplierZ;
            if(multiplierZ < 0) {
                minZ = Math.min(minZ, maxZ);
                maxZ = Math.max(minZ, maxZ);
            }
        }
        return this;
    }

    @Contract("_ -> this")
    public BoundingBox multiply(double multiplier){
        return multiply(multiplier, multiplier, multiplier);
    }

    @Contract("_ -> this")
    public BoundingBox multiply(Vector multi){
        return multiply(multi.getX(), multi.getY(), multi.getZ());
    }

    @Contract("_, _, _ -> this")
    public BoundingBox divide(double divisorX, double divisorY, double divisorZ){
        if(divisorX == 0) minX = maxX = 0;
        else {
            minX /= divisorX;
            maxX /= divisorX;
            if(divisorX < 0) {
                minX = Math.min(minX, maxX);
                maxX = Math.max(minX, maxX);
            }
        }
        if(divisorY == 0) minY = maxY = 0;
        else {
            minY /= divisorY;
            maxY /= divisorY;
            if(divisorY < 0) {
                minY = Math.min(minY, maxY);
                maxY = Math.max(minY, maxY);
            }
        }
        if(divisorZ == 0) minZ = maxZ = 0;
        else {
            minZ /= divisorZ;
            maxZ /= divisorZ;
            if(divisorZ < 0) {
                minZ = Math.min(minZ, maxZ);
                maxZ = Math.max(minZ, maxZ);
            }
        }
        return this;
    }

    @Contract("_ -> this")
    public BoundingBox divide(double divisor){
        return divide(divisor, divisor, divisor);
    }

    @Contract("_ -> this")
    public BoundingBox divide(Vector divisor){
        return divide(divisor.getX(), divisor.getY(), divisor.getZ());
    }

    public boolean contains(double x, double y, double z) {
        return (x >= this.minX && x <= this.maxX) &&
                (y >= this.minY && y <= this.maxY) &&
                (z >= this.minZ && z <= this.maxZ);
    }

    public boolean contains(@NotNull Vector v) {
        Condition.argNotNull("v", v);
        return contains(v.getX(), v.getY(), v.getZ());
    }

    public boolean contains(@NotNull Location loc) {
        Condition.argNotNull("loc", loc);
        return contains(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean contains(@NotNull BoundingBox box) {
        Condition.argNotNull("box", box);
        return (this.minX <= box.minX) && (this.maxX >= box.maxX) &&
                (this.minY <= box.minY) && (this.maxY >= box.maxY) &&
                (this.minZ <= box.minZ) && (this.maxZ >= box.maxZ);
    }

    public boolean intersect(@NotNull BoundingBox box) {
        Condition.argNotNull("box", box);
        return ((minX <= box.maxX) && (maxX >= box.minX)) ||
                ((minY <= box.maxY) && (maxY >= box.minY)) ||
                ((minZ <= box.maxZ) && (maxZ >= box.minZ));
    }

    @NotNull
    public BoundingBox duplicate(){
        return new BoundingBox(this);
    }

    @NotNull
    public Vector[] getVectorCorners(){
        return new Vector[]{
                new Vector(minX, minY, minZ), // 123
                new Vector(minX, maxY, maxZ), // 156
                new Vector(maxX, minY, minZ), // 423
                new Vector(minX, maxY, minZ), // 153
                new Vector(minX, minY, maxZ), // 126
                new Vector(maxX, minY, maxZ), // 426
                new Vector(maxX, maxY, minZ), // 453
                new Vector(maxX, maxY, maxZ)  // 456
        };
    }

    @NotNull
    public Location[] getLocationCorners(@Nullable World world){
        return new Location[]{
                new Location(world, minX, minY, minZ),
                new Location(world, minX, maxY, maxZ),
                new Location(world, maxX, minY, minZ),
                new Location(world, minX, maxY, minZ),
                new Location(world, minX, minY, maxZ),
                new Location(world, maxX, minY, maxZ),
                new Location(world, maxX, maxY, minZ),
                new Location(world, maxX, maxY, maxZ)
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return Double.compare(that.minX, minX) == 0 &&
                Double.compare(that.minY, minY) == 0 &&
                Double.compare(that.minZ, minZ) == 0 &&
                Double.compare(that.maxX, maxX) == 0 &&
                Double.compare(that.maxY, maxY) == 0 &&
                Double.compare(that.maxZ, maxZ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
