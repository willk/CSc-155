package objects;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class Camera {
    private Vector3D u, v, n, l;
    private Matrix3D i;

    public Camera() {
        init();
        l = new Vector3D(0, 0, 3);
    }

    public Camera(float x, float y, float z) {
        init();
        l = new Vector3D(x, y, z);
    }

    private void init() {
        u = new Vector3D(1, 0, 0, 0);
        v = new Vector3D(0, 1, 0, 0);
        n = new Vector3D(0, 0, 1, 0);

        i = new Matrix3D(new double[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }

    /*
     * Get the x location of the camera.
     */
    public double getX() {
        return l.getX();
    }

    /*
     * Get the y location of the camera.
     */
    public double getY() {
        return l.getY();
    }

    /*
     * Get the z location of the camera.
     */
    public double getZ() {
        return l.getZ();
    }

    /*
     * Move forward or backwards along the Z axis without thinking about being in the negative z.
     * Camera is moving along the N vector.
     * @param dz - negative for backwards, positive for forwards.
     */
    public void movez(double dz) {
        Vector3D c = this.n;
        Vector3D p = c.normalize().mult(-dz);
        l = l.add(p);
    }

    /*
     * Move left or right along the X axis.
     * Camera is moving along the U vector.
     * @param dx - negative for left, positive for right
     */
    public void movex(double dx) {
        Vector3D c = this.u;
        Vector3D p = c.normalize().mult(dx);
        l = l.add(p);
    }

    /*
     * Move up or down along the Y axis.
     * Camera is moving along the V vector.
     * @param dy - negative for down, positive for up.
     */
    public void movey(double dy) {
        Vector3D c = this.v;
        Vector3D p = c.normalize().mult(dy);
        l = l.add(p);
    }

    /*
     * Pan up or down along the v vector
     * @ param d - positive for left, negative for right.
     */
    public void pan(double d) {
        Matrix3D r = new Matrix3D();
        r.rotate(d, this.v);

        u = u.mult(r);
        n = n.mult(r);
    }

    /*
     * Pitch up and down around the u vector.
     * @param d - negative for down, positive for up.
      */
    public void pitch(double d) {
        Matrix3D r = new Matrix3D();
        r.rotate(d, this.u);

        n = n.mult(r);
        v = v.mult(r);
    }


    public Matrix3D getVTM() {
        Matrix3D r = (Matrix3D) i.clone(), t = (Matrix3D) i.clone();

        r.setCol(3, new Vector3D(-l.getX(), -l.getY(), -l.getZ(), 1));

        t.setRow(0, u);
        t.setRow(1, v);
        t.setRow(2, n);

        t.concatenate(r);

        return t;
    }
}