package a2.objects;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

/**
 * u: left (-) and right (+),
 * v: down (-) and up (+),
 * n: backward (-) and forward (+)
 */
public class Camera {
    private Point3D l;
    private Vector3D u, v, n;

    public Camera() {
        l = new Point3D(0.0, 0.0, 0.0);
        u = new Vector3D();
        v = new Vector3D();
        n = new Vector3D();
    }

    public void pitch(float d) {

    }

    public void yaw(float d) {

    }

    public void roll(float d) {

    }

    public void foward(float d) {
        Vector3D l = new Vector3D(getLocation());
        Vector3D move = new Vector3D(n.getX(), n.getY(), n.getZ()).normalize().mult(d);
        setLocation(l.add(move));
    }

    private void back(float d) {
        Vector3D l = new Vector3D(getLocation());
    }

    private Point3D getLocation() {
        return new Point3D(l.getX(), l.getY(), l.getZ());
    }

    private void setLocation(Vector3D l) {
        this.l.setX(l.getX());
        this.l.setY(l.getY());
        this.l.setZ(l.getZ());
        this.l.setW(1.0f);
    }
}
