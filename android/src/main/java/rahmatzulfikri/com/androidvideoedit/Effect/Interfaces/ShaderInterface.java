package rahmatzulfikri.com.androidvideoedit.Effect.Interfaces;

/**
 * An interface that every effect must implement so that there is a common
 * getShader method that every effect class is force to override
 *
 * @author sheraz.khilji
 */
public interface ShaderInterface {

    public String getImageShader();
    public String getVideoShader();

}
