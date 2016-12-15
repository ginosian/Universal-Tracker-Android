package com.margin.mgms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.margin.camera.models.Photo;
import com.margin.mgms.R;

import java.util.ArrayList;

/**
 * Created on May 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotosActivity extends AppCompatActivity {

    /**
     * For testing purposes.
     * <p>
     * TODO: remove later
     */

    public static final String[] IMAGES_URI = new String[]{
            "http://blog.sargeslist.com/wp-content/uploads/2014/06/Care-Package.jpg",
            "http://www.candymachines.com/images/bulk_candy/rascals-fruit-shape-candy.jpg",
            "http://s7.orientaltrading.com/is/image/OrientalTrading/CANDY-Buffet-031616?$NOWA$&$1x1main$&",
            "http://www.candymachines.com/images/bulk_candy/cotton-candy-995.jpg",
            "http://cf067b.medialib.glogster.com/media/39/39681f75bad96653465b1006098e44fe494b00f255a4e44a4ed938eab49a56f6/candy.jpg",
            "http://www.groovycandies.com/media/catalog/product/cache/1/image/650x/040ec09b1e35df139433887a97daa66f/b/o/bonz-coated-candy-dog-bones.jpg",
            "http://www.groovycandies.com/media/catalog/product/cache/1/image/650x/040ec09b1e35df139433887a97daa66f/c/o/cosmic-rock-sweet-tart-bulk-candy.jpg",
            "https://s-media-cache-ak0.pinimg.com/736x/e8/c8/75/e8c8751e656abccd0220e7bd123c65f4.jpg",
            "http://kingstoncandybar.com/wp-content/uploads/2015/11/candy-buttons.jpg",
            "http://www.candymachines.com/images/bulk_candy/oh-baby-pacifiers-candy.jpg",
            "http://g01.a.alicdn.com/kf/HTB1.A7GKpXXXXbkXXXXq6xXFXXXg/Luxury-Cohiba-Behiki-cigar-box-with-velvet-bag-and-gift-package.jpg",
            "http://www.ikare.com/wp-content/uploads/2013/11/Art-of-Appreciation-Gift-Baskets-Coffee-Lovers-Care-Package-Gift-Box-1.jpg",
            "http://g02.a.alicdn.com/kf/HTB17rtBIVXXXXXoaXXXq6xXFXXXq/10-21-5-6cm-Brown-Kraft-Paper-Bellows-Pocket-Package-Box-W-Window-Stand-Up-Folded.jpg",
            "http://nerdapproved.com/wp-content/uploads/2012/12/Indy-Jones-package-1.jpg",
            "http://3.bp.blogspot.com/-75LJVZburJY/TiIz8SUMesI/AAAAAAAAAAA/sy4xVCvsGqw/s1600/christmas-package-decorations.jpg",
            "http://g01.a.alicdn.com/kf/HTB1eleYJpXXXXX5XpXXq6xXFXXXG/square-pink-bronzier-moon-cake-box-packaging-dessert-package-decoration-supplies.jpg",
            "http://christmastreeornaments.org/wp-content/uploads/Package-of-4-Unfinished-Wood-Ultra-Miniature-Train-Engine-Ornaments-for-Crafting-and-Embellishing-0.jpg",
            "http://www.wwemotors.com/_img/hyundai-pump-jack-drive-package.png"
    };
    private static final ArrayList<Photo> PHOTO_LIST;

    static {
        PHOTO_LIST = new ArrayList<>();

        Photo photo;
        for (String imageUri : IMAGES_URI) {
            photo = new Photo();
            photo.setImagePath(imageUri);
            PHOTO_LIST.add(photo);
        }
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, PhotosActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        if (null == savedInstanceState) {
//            FragmentUtils.addRootFragment(this, R.id.content_frame,
//                    PhotoCardFragment.newFragment(PHOTO_LIST), PhotoCardFragment.TAG);
//            FragmentUtils.addRootFragment(this, R.id.content_frame,
//                    PhotoCardFragment.newFragment(new ArrayList<>()), PhotoCardFragment.TAG);
        }
    }

}
