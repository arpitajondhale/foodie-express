import { useState, useEffect } from 'react';
import { api } from '../services/api';
import DishCard from '../components/ui/DishCard';
import CustomizationModal from '../components/ui/CustomizationModal';
import { useHealthProfile } from '../context/HealthProfileContext';
import {
  Search,
  SlidersHorizontal,
  Star,
  Clock,
  ShieldCheck,
  ChevronRight,
  Flame,
  Award,
} from 'lucide-react';

export default function CustomerStorefront({ onSelectRestaurant, selectedRestaurantId }) {
  const { safeOnly, setSafeOnly, profile } = useHealthProfile();
  const [restaurants, setRestaurants] = useState([]);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [menuCategories, setMenuCategories] = useState([]);
  const [activeCategory, setActiveCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [dietFilter, setDietFilter] = useState('ALL');
  const [customizingDish, setCustomizingDish] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch all restaurants on mount
  useEffect(() => {
    loadRestaurants();
  }, []);

  // When selected restaurant changes, load menu
  useEffect(() => {
    if (selectedRestaurantId) {
      loadRestaurantMenu(selectedRestaurantId);
    }
  }, [selectedRestaurantId]);

  const loadRestaurants = async () => {
    try {
      setLoading(true);
      const data = await api.getRestaurants();
      setRestaurants(data);
      if (data.length > 0 && !selectedRestaurantId) {
        loadRestaurantMenu(data[0].id);
      }
    } finally {
      setLoading(false);
    }
  };

  const loadRestaurantMenu = async (restaurantId) => {
    try {
      const rest = await api.getRestaurantById(restaurantId);
      setSelectedRestaurant(rest);
      const menu = await api.getRestaurantMenu(restaurantId);
      setMenuCategories(menu);
      setActiveCategory('ALL');
    } catch {
      // Ignored
    }
  };

  // Collect all dishes
  const allDishes = menuCategories.flatMap((c) => c.items || []);

  // Filter dishes
  const filteredDishes = allDishes.filter((dish) => {
    // Category match
    if (activeCategory !== 'ALL' && dish.categoryName !== activeCategory) {
      return false;
    }

    // Search query match
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase().trim();
      const matchName = dish.name.toLowerCase().includes(q);
      const matchDesc = dish.description?.toLowerCase().includes(q);
      const matchCat = dish.categoryName?.toLowerCase().includes(q);
      if (!matchName && !matchDesc && !matchCat) return false;
    }

    // Diet filter
    if (dietFilter !== 'ALL' && dish.dietType !== dietFilter) {
      return false;
    }

    // Safe For My Health filter
    if (safeOnly && profile) {
      const nut = dish.nutritionalInfo;
      if (!nut) return false;

      // Allergy conflict check
      if (profile.allergies?.some((a) => nut.allergens?.includes(a))) return false;

      // Contraindicated condition check
      if (profile.conditions?.some((c) => nut.contraindicatedConditions?.includes(c))) return false;

      // Macro limit check
      if (profile.conditions?.includes('DIABETES') && nut.sugarGrams > (profile.maxSugarGramsPerMeal || 15)) {
        return false;
      }
      if (profile.conditions?.includes('HYPERTENSION') && nut.sodiumMg > (profile.maxSodiumMgPerMeal || 600)) {
        return false;
      }
    }

    return true;
  });

  return (
    <div className="storefront-page">
      {/* Top Health Hero Banner */}
      <div className="store-hero">
        <div className="hero-content">
          <div className="hero-badge">
            <Award size={16} /> India's First Clinical Health-Aware Food Platform
          </div>
          <h1>Eat What You Love. Guard Your Health.</h1>
          <p>
            Zero sugar spikes. Heart-safe sodium levels. Automated allergen protection tailored to your medical profile.
          </p>

          {/* Search Bar */}
          <div className="hero-search-bar">
            <Search size={20} className="search-icon" />
            <input
              type="text"
              placeholder="Search dishes (e.g. Ragi Dosa, Paneer, Chicken Tikka, Keto Bowl)..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            {searchQuery && (
              <button className="clear-btn" onClick={() => setSearchQuery('')}>
                ✕
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Restaurant Carousel Selector */}
      <section className="restaurants-section">
        <div className="section-header">
          <div>
            <h3>Top Partner Kitchens</h3>
            <span className="section-sub">Hygiene-certified with clinical nutritional tracking</span>
          </div>
        </div>

        <div className="restaurants-slider">
          {restaurants.map((rest) => {
            const isSelected = selectedRestaurant?.id === rest.id;
            return (
              <div
                key={rest.id}
                className={`restaurant-card ${isSelected ? 'selected' : ''}`}
                onClick={() => loadRestaurantMenu(rest.id)}
              >
                <div className="rest-img-wrap">
                  <img src={rest.imageUrl} alt={rest.name} className="rest-img" />
                  <span className="rest-time-badge">
                    <Clock size={12} /> {rest.avgPrepTimeMins} mins
                  </span>
                </div>
                <div className="rest-info">
                  <h4 className="rest-name">{rest.name}</h4>
                  <p className="rest-cuisines">{Array.from(rest.cuisineTypes || []).join(' • ')}</p>
                  <div className="rest-footer">
                    <span className="rating-tag">★ {rest.rating} ({rest.totalRatings}+)</span>
                    <span className="fssai-pill">FSSAI Certified</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </section>

      {/* Selected Restaurant Menu View */}
      {selectedRestaurant && (
        <section className="menu-container">
          {/* Restaurant Header Banner */}
          <div className="restaurant-detail-banner">
            <div className="detail-left">
              <h2>{selectedRestaurant.name}</h2>
              <p className="detail-cuisines">
                {Array.from(selectedRestaurant.cuisineTypes || []).join(', ')} • {selectedRestaurant.address}
              </p>
              <div className="detail-meta">
                <span className="rating-box">★ {selectedRestaurant.rating}</span>
                <span className="meta-sep">•</span>
                <span>{selectedRestaurant.avgPrepTimeMins} mins delivery</span>
                <span className="meta-sep">•</span>
                <span className="fssai-box">FSSAI: {selectedRestaurant.fssaiLicense}</span>
              </div>
            </div>

            {/* Health Guard Status Banner for Restaurant */}
            <div className="detail-health-box">
              <ShieldCheck size={24} className="health-shield-icon" />
              <div>
                <span className="health-box-title">Dietary Intelligence Active</span>
                <p className="health-box-sub">
                  Every item shows macro badges (Calories, Sugar, Sodium, GI) & allergen warnings.
                </p>
              </div>
            </div>
          </div>

          {/* Filter Bar */}
          <div className="menu-filters-bar">
            {/* Category Pills */}
            <div className="category-scroll-list">
              <button
                className={`cat-pill ${activeCategory === 'ALL' ? 'active' : ''}`}
                onClick={() => setActiveCategory('ALL')}
              >
                All Menu ({allDishes.length})
              </button>
              {menuCategories.map((c) => (
                <button
                  key={c.id}
                  className={`cat-pill ${activeCategory === c.name ? 'active' : ''}`}
                  onClick={() => setActiveCategory(c.name)}
                >
                  {c.name} ({c.items?.length || 0})
                </button>
              ))}
            </div>

            {/* Diet Type Filters */}
            <div className="diet-filter-buttons">
              <button
                className={`diet-btn ${dietFilter === 'ALL' ? 'active' : ''}`}
                onClick={() => setDietFilter('ALL')}
              >
                All
              </button>
              <button
                className={`diet-btn ${dietFilter === 'VEG' ? 'active' : ''}`}
                onClick={() => setDietFilter('VEG')}
              >
                Veg Only
              </button>
              <button
                className={`diet-btn ${dietFilter === 'NON_VEG' ? 'active' : ''}`}
                onClick={() => setDietFilter('NON_VEG')}
              >
                Non-Veg
              </button>
              <button
                className={`diet-btn ${dietFilter === 'VEGAN' ? 'active' : ''}`}
                onClick={() => setDietFilter('VEGAN')}
              >
                Vegan
              </button>
            </div>
          </div>

          {/* Safe for Health Notice */}
          {safeOnly && (
            <div className="safe-filter-notice">
              <ShieldCheck size={18} className="text-success" />
              <span>
                Showing only dishes safe for your active health conditions (
                {profile?.conditions?.map((c) => c.replace(/_/g, ' ')).join(', ') || 'General Healthy'}).
              </span>
              <button className="text-btn" onClick={() => setSafeOnly(false)}>
                Show All Dishes
              </button>
            </div>
          )}

          {/* Dishes Grid */}
          <div className="dishes-grid">
            {filteredDishes.length > 0 ? (
              filteredDishes.map((dish) => (
                <DishCard
                  key={dish.id}
                  dish={dish}
                  onOpenCustomization={setCustomizingDish}
                />
              ))
            ) : (
              <div className="empty-dishes-view">
                <p>No dishes found matching your current filters.</p>
                {safeOnly && (
                  <button className="btn-secondary" onClick={() => setSafeOnly(false)}>
                    Turn Off "Safe For My Health" Filter
                  </button>
                )}
              </div>
            )}
          </div>
        </section>
      )}

      {/* Dish Customization Modal */}
      {customizingDish && (
        <CustomizationModal
          foodItem={customizingDish}
          onClose={() => setCustomizingDish(null)}
        />
      )}
    </div>
  );
}