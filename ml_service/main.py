from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

app = FastAPI(title="Airport ML Microservice")

class RecommendationResponse(BaseModel):
    passenger_id: str
    recommended_product_ids: List[int]

@app.get("/api/v1/recommendations/{passenger_id}", response_model=RecommendationResponse)
def get_recommendations(passenger_id: str):
    """
    Returns a list of recommended product IDs for the given passenger.
    This is a mock implementation.
    """
    # Simple mock logic based on hashed passenger_id string
    if passenger_id == "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5": # 12345
        # Recommend Toblerone and Chanel
        recommended_ids = [2, 3]
    elif passenger_id == "e2217d3e4e120c6a3372a1890f03e232b35ad659d71f7a62501a4ee204a3e66d": # 67890
        # Recommend Whisky and Toblerone
        recommended_ids = [1, 2]
    else:
        # Default recommendation
        recommended_ids = [1]
        
    return RecommendationResponse(
        passenger_id=passenger_id,
        recommended_product_ids=recommended_ids
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
