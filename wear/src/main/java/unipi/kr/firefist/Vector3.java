package unipi.kr.firefist;

/**
 * Created by KimHeekue on 2014-09-20.
 */
public class Vector3 {

    public float x;
    public float y;
    public float z;


    // Constructors
    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    @Override
    public Vector3 clone()
    {
        return new Vector3(x, y, z);
    }

    // setters
    public void set(Vector3 v)
    {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }



    // Vector operations
    public Vector3 add(Vector3 v)
    {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 sub(Vector3 v)
    {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 mul(float s)
    {
        return new Vector3(x * s, y * s, z * s);
    }

    public Vector3 div(float s)
    {
        return new Vector3(x / s, y / s, z / s);
    }

    public float dot(Vector3 v)
    {
        return x * v.x + y * v.y + z * v.z;
    }

    public float length()
    {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float sqrlen()
    {
        return x * x + y * y + z * z;
    }

    public Vector3 getNormalized()
    {
        float len = length();
        return new Vector3(x / len, y / len, z / len);
    }

    public Vector3 normalize()
    {
        float len = length();
        x /= len;
        y /= len;
        z /= len;
        return this;
    }

}
