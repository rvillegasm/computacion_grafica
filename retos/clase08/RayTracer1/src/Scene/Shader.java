/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scene;

import java.util.Vector;

import Math.Point;
import Math.Vector4;
import Math.Ray;

/**
 *
 * @author htrefftz
 */
public class Shader {
    /**
     * Computes the color of a point "point", on a surface with normal "normal",
     * given the material properties of the object "material"
     * @param point 3D coordinates of the point
     * @param normal normal of the surface at point "point"
     * @param material material  of the object 
     * @return 
     */
    public static Colour computeColor(Point point, Vector4 normal, Material material) {
        normal.normalize();
        // We will add all the colors in acum
        Colour acum = new Colour(1, 0, 0);
        // Compute the Ambient Reflection
        Colour AmbientReflection = Colour.multiply(Colour.multiply(Scene.ambientLight.color, material.color), 
                material.Ka);
        acum = Colour.add(acum, AmbientReflection);
        // Compute the Diffuse Reflection, respect to all point lights
        for(PointLight pl: Scene.pointLights) {
            Vector4 light = new Vector4(point, pl.point);
            Ray shadowRay = new Ray(point, light);
            // Check if the object is in the shadow with respect to this source
            // of life. If it is, do not add diffuse reflection
            if(!Scene.intersectRayForShadow(shadowRay)) {
                light.normalize();
                // get the light reflection
                Vector4 reflection = Vector4.reflection(light, normal);
                // get the view vector
                Point observer = new Point(0, 0, 0);
                Vector4 v = new Vector4(point, observer);
                v.normalize();
                reflection.normalize();
                // Acá se debe agregar el producto entre normal y light (***)
                // ---------------------------------------------------------------------------------------------------------------
                double scalar = Vector4.dotProduct(light, normal) * material.Kd;
                // ---------------------------------------------------------------------------------------------------------------
                // If dot product is < 0, the point is not receiving light from
                // this source.
                if(scalar < 0) scalar = 0;
                Colour DiffuseReflection = Colour.multiply(Colour.multiply(pl.color, material.color), 
                        scalar);
                acum = Colour.add(acum, DiffuseReflection);

                // compute the specular reflection
                double scalarS = material.Ks * Math.pow(Vector4.dotProduct(reflection, v), material.n);
                // If the angle between light and normal is more than 90, the point is not recieving light
                // from this source
                double lightNormalAngle = Vector4.getAngle(light, normal); 
                if(lightNormalAngle < 90 && lightNormalAngle > 0){
                    Colour SpecularReflection = Colour.multiply(pl.color, scalarS);
                    acum = Colour.add(acum, SpecularReflection);
                }
            }
        }
        
        return acum;
    }
}
